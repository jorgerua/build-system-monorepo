## Context

This is a greenfield PIX payment processing service built inside the monorepo. PIX is Brazil's mandatory instant payment scheme operated by Banco Central do Brasil (BCB). The system must integrate with two external BCB systems: **SPI** (payment settlement rails) and **DICT** (PIX key directory).

The monorepo has no packages yet. This design establishes the foundational architecture that all subsequent packages will follow. The primary driver is validating the build system across multiple languages, so the architecture must span Go, C#, and Java deliberately — not because one language is insufficient.

## Goals / Non-Goals

**Goals:**
- Define how the five capabilities (payment-initiation, payment-receiving, key-management, payment-status, notifications) map to packages and services
- Assign a primary language to each package with rationale
- Define the inter-service communication style
- Define the data model for payments and keys
- Establish test strategy across unit, integration, and performance tiers

**Non-Goals:**
- Full BCB regulatory compliance (the service is a test harness, not a production PSP)
- Real SPI/DICT connectivity (stubs/simulators are sufficient)
- Multi-tenant or multi-institution support
- UI or end-user dashboard

## Decisions

### 1. Package-to-language mapping

| Package | Language | Rationale |
|---|---|---|
| `payment-core` (initiation + receiving + status) | Go | High-throughput, low-latency path; Go's goroutines suit concurrent payment processing |
| `api-gateway` | C# | ASP.NET Core is idiomatic for RESTful APIs with rich middleware; exercises the C# build chain |
| `key-management` | Java | DICT interactions are XML/SOAP-heavy; Java's ecosystem (JAX-WS, Spring) handles this well |
| `notification-worker` | Go | Event fan-out is I/O-bound; Go channels and goroutines are a natural fit |
| `integration-tests` | Robot Framework | Language-agnostic; tests the full stack end-to-end regardless of internal language |
| `perf-tests` | K6 (JS) | K6 is the standard choice; sits outside the language matrix |

**Alternative considered**: Single Go service for everything. Rejected — the monorepo's stated goal is multi-language build validation.

### 2. Inter-service communication

Internal services communicate over **gRPC** (protobuf contracts). The `api-gateway` (C#) translates inbound REST to gRPC calls toward `payment-core` and `key-management`.

**Alternative considered**: REST between all services. Rejected — gRPC enforces schema contracts and generates typed clients in all three languages from a single `.proto` file.

### 3. Async event flow for notifications

`payment-core` publishes domain events to an **in-process event bus** (Go channels in the same process, wrapped behind an interface). `notification-worker` subscribes and delivers webhooks.

For integration tests, the bus can be swapped for a simple in-memory queue without changing production code.

**Alternative considered**: External message broker (Kafka/RabbitMQ). Deferred — adds operational overhead that is unnecessary for a build-system test harness. The interface abstraction makes it easy to introduce later.

### 4. Data storage

Each service owns its own SQLite database file during development and testing. SQLite requires no external process, making local runs and CI simple.

**Alternative considered**: PostgreSQL. Deferred for the same reason as the message broker — complexity without benefit at this stage.

### 5. Payment state machine

Payments follow a strict state machine enforced in `payment-core`:

```
CREATED → VALIDATING → SUBMITTED → SETTLED
                   ↘ FAILED
SETTLED → REVERSING → REVERSED
```

State transitions are the only mutations allowed; the record is append-only in the events log.

### 6. PIX key types supported

Registration, lookup, portability, and deletion for: CPF, CNPJ, e-mail, phone (`+55` E.164), and random UUID keys — matching BCB's DICT specification.

## Risks / Trade-offs

- **gRPC in C# ↔ Go**: Protobuf code generation must be wired into both build chains. If the monorepo build system doesn't support it natively, a pre-build script will be needed. → *Mitigation*: Commit generated files to source control initially; replace with build-time generation once tooling is stable.
- **SQLite concurrency**: SQLite's write serialisation will bottleneck performance tests. → *Mitigation*: Performance tests target the Go core directly, bypassing the gateway; WAL mode is enabled by default.
- **SPI/DICT simulators drift from spec**: Stubs may not reflect real BCB behaviour. → *Mitigation*: Simulators are kept minimal and clearly labelled as test doubles; real integration is out of scope.
- **Cross-language dependency graph**: Build ordering (proto generation → Go/C#/Java compilation) must be correct. → *Mitigation*: Enforce proto generation as the first step in the build; document it in CLAUDE.md once tooling is settled.

## Migration Plan

1. Add monorepo tooling (package manager, workspace config) — prerequisite, tracked separately
2. Create `proto/` directory with shared `.proto` definitions
3. Scaffold `payment-core` (Go) with state machine and gRPC server
4. Scaffold `api-gateway` (C#) with REST→gRPC translation
5. Scaffold `key-management` (Java) with DICT stub client
6. Scaffold `notification-worker` (Go)
7. Wire unit tests in each package
8. Wire `integration-tests` (Robot Framework) against the running stack
9. Wire `perf-tests` (K6) against `api-gateway`

Rollback: each step is an independent commit; revert any step without affecting others.

## Open Questions

- Which monorepo build tool will be used (Bazel, Nx, Turborepo, Make)? The answer affects how proto generation is wired.
- Should the gRPC proto files live in a dedicated package or be co-located with the consumer packages?
- What port conventions should services use locally (to avoid conflicts in CI)?
