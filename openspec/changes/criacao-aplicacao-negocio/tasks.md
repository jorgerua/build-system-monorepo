## 1. Monorepo Foundation

- [x] 1.1 Choose and configure the monorepo build tool (Bazel, Nx, Turborepo, or Make) and document the decision in CLAUDE.md
- [x] 1.2 Create the workspace config file (e.g., `pnpm-workspace.yaml`, `go.work`, or equivalent) listing all packages
- [x] 1.3 Create `proto/` directory at the repo root and establish the proto generation pipeline as the first build step
- [x] 1.4 Document local port assignments for each service (api-gateway, payment-core, key-management, notification-worker)

## 2. Proto Definitions

- [x] 2.1 Define `payment.proto` with `CreatePayment`, `GetPayment`, `ListPayments`, and `SubmitToSPI` RPC methods
- [x] 2.2 Define `keys.proto` with `RegisterKey`, `LookupKey`, `DeleteKey`, and `InitiatePortability` / `ConfirmPortability` RPC methods
- [x] 2.3 Define `events.proto` with `PaymentEvent` message used by the notification worker
- [x] 2.4 Generate Go, C#, and Java stubs from the proto files and commit generated code to source control

## 3. payment-core (Go)

- [x] 3.1 Scaffold `payment-core` Go module with `go.mod`, folder structure (`cmd/`, `internal/`)
- [x] 3.2 Implement the payment state machine with all valid transitions (`CREATED → VALIDATING → SUBMITTED → SETTLED`, `FAILED`, `REVERSING → REVERSED`)
- [x] 3.3 Implement the append-only event log (SQLite, WAL mode enabled)
- [x] 3.4 Implement `CreatePayment` handler: field validation, idempotency key deduplication, amount limit enforcement (R$ 0.01 – R$ 999,999,999.99)
- [x] 3.5 Implement SPI stub client and `SubmitToSPI` logic with state transitions on success and rejection
- [x] 3.6 Implement `GetPayment` handler: query by ID, 404 on missing
- [x] 3.7 Implement `ListPayments` handler: filter by state, cursor-based pagination (default 20, max 100), ordered by creation timestamp descending
- [x] 3.8 Implement inbound credit handler: idempotency on SPI end-to-end ID, payee key ownership check, 10-second confirmation timeout
- [x] 3.9 Implement in-process event bus (Go channel behind interface) publishing `PaymentEvent` on every state transition
- [x] 3.10 Expose gRPC server on configured port
- [x] 3.11 Write unit tests (Testify) covering state machine, idempotency, validation, and pagination logic

## 4. key-management (Java)

- [x] 4.1 Scaffold `key-management` Java/Spring Boot project with `build.gradle` or `pom.xml`
- [x] 4.2 Implement key format validators for CPF, CNPJ, e-mail, phone (E.164 `+55`), and random UUID
- [x] 4.3 Implement `RegisterKey`: validate format, reject duplicates (`KEY_ALREADY_EXISTS`), generate UUID v4 for random type, persist to SQLite, call DICT stub
- [x] 4.4 Implement DICT stub client (in-memory or file-based, labelled as test double)
- [x] 4.5 Implement `LookupKey`: proxy to DICT stub, 60-second in-memory cache, 404 on miss
- [x] 4.6 Implement `DeleteKey`: ownership check (`KEY_OWNERSHIP_MISMATCH`), propagate deletion to DICT, 404 on miss
- [x] 4.7 Implement `InitiatePortability`: create PENDING claim, simulate challenge to current owner
- [x] 4.8 Implement `ConfirmPortability`: reassign key on confirmation, cancel claim after 7-day expiry
- [x] 4.9 Expose gRPC server on configured port
- [x] 4.10 Write unit tests (JUnit) covering all key types, duplicate detection, cache behaviour, ownership checks, and portability flow

## 5. api-gateway (C#)

- [x] 5.1 Scaffold `api-gateway` ASP.NET Core project with `api-gateway.csproj`
- [x] 5.2 Wire gRPC clients for `payment-core` and `key-management` using generated C# stubs
- [x] 5.3 Implement REST endpoints for payment initiation: `POST /payments`, mapping to `CreatePayment` gRPC call
- [x] 5.4 Implement REST endpoints for payment receiving: `POST /payments/inbound`, mapping to inbound credit handler
- [x] 5.5 Implement REST endpoints for payment status: `GET /payments/{id}` and `GET /payments?state=&cursor=`
- [x] 5.6 Implement REST endpoints for key management: `POST /keys`, `GET /keys/{key}`, `DELETE /keys/{key}`, `POST /keys/{key}/portability`, `POST /keys/{key}/portability/confirm`
- [x] 5.7 Implement REST endpoints for webhook management: `POST /webhooks`, `DELETE /webhooks/{id}`
- [x] 5.8 Map gRPC error codes to HTTP status codes and structured JSON error bodies
- [x] 5.9 Write unit tests (xUnit) covering request mapping, error translation, and HTTP contract for each endpoint

## 6. notification-worker (Go)

- [x] 6.1 Scaffold `notification-worker` Go module
- [x] 6.2 Implement webhook registration: persist URL + event type subscriptions, probe URL for reachability before confirming, validate HTTPS-only
- [x] 6.3 Implement event bus subscriber that receives `PaymentEvent` from `payment-core`
- [x] 6.4 Implement HTTP POST delivery to registered webhook URLs (HTTPS only, 5-second timeout per attempt)
- [x] 6.5 Implement exponential backoff retry (up to 5 attempts: 1s, 2s, 4s, 8s, 16s), marking `PERMANENTLY_FAILED` after exhaustion
- [x] 6.6 Implement delivery audit log (SQLite): record attempt number, timestamp, HTTP status, outcome
- [x] 6.7 Implement 7-day audit log retention cleanup job
- [x] 6.8 Write unit tests (Testify) covering delivery, retry backoff, ownership filtering by event type, audit logging, and retention

## 7. Integration Tests (Robot Framework)

- [x] 7.1 Scaffold `integration-tests` package with Robot Framework project structure
- [x] 7.2 Write test suite for payment initiation happy path: create → submit → settled flow
- [x] 7.3 Write test suite for inbound payment receiving: credit notification, idempotency, unknown key rejection
- [x] 7.4 Write test suite for key management: register, lookup (cache hit/miss), delete, portability lifecycle
- [x] 7.5 Write test suite for notifications: webhook registration, event delivery, retry on failure
- [x] 7.6 Wire integration tests into CI so they run against all services started together

## 8. Performance Tests (K6)

- [x] 8.1 Scaffold `perf-tests` package with K6 project structure
- [x] 8.2 Write K6 script for `POST /payments` throughput test (target: baseline TPS with p99 < 200ms)
- [x] 8.3 Write K6 script for `GET /payments/{id}` read latency test
- [x] 8.4 Write K6 script for `GET /keys/{key}` lookup with cache warm and cold scenarios
- [x] 8.5 Wire performance tests into CI as an optional stage with threshold assertions

## 9. Build System Integration

- [x] 9.1 Add `payment-core` to the monorepo build with lint (`golangci-lint`) and test targets
- [x] 9.2 Add `notification-worker` to the monorepo build with lint and test targets
- [x] 9.3 Add `key-management` to the monorepo build with lint (Checkstyle/SpotBugs) and test targets
- [x] 9.4 Add `api-gateway` to the monorepo build with lint (Roslyn analysers) and test targets
- [x] 9.5 Add `integration-tests` and `perf-tests` to the monorepo build as separate stages
- [x] 9.6 Verify that a clean build from scratch (proto generation → all packages → all tests) succeeds in CI
- [x] 9.7 Update CLAUDE.md with install, build, test, and lint commands for the complete repo
