# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

PIX payment processing service — a multi-language business application used to validate the monorepo build system, CI/CD pipeline, and cross-language tooling. PIX is Brazil's mandatory instant payment scheme (Banco Central do Brasil).

## Build System

**Tool: GNU Make** (chosen for universal language support — Go, Java, C#, Robot Framework, K6)

The build runs in this order: `proto-gen → packages → tests`

### Install Dependencies

```bash
# Go (payment-core, notification-worker)
cd payment-core        && go mod download
cd notification-worker && go mod download

# Java (key-management) — Gradle wrapper downloads dependencies automatically
cd key-management && ./gradlew dependencies

# C# (api-gateway) — NuGet packages restored automatically on build
cd api-gateway && dotnet restore

# Robot Framework (integration-tests)
cd integration-tests && pip install -r requirements.txt

# K6 (perf-tests) — install k6 separately: https://k6.io/docs/getting-started/installation/
```

### Build Commands

| Command                            | What it does                                |
|------------------------------------|---------------------------------------------|
| `make proto-gen`                   | Generate Go/C#/Java stubs from .proto files |
| `make build`                       | Build all packages (proto-gen runs first)   |
| `make payment-core-build`          | Build payment-core only                     |
| `make key-management-build`        | Build key-management only                   |
| `make api-gateway-build`           | Build api-gateway only                      |
| `make notification-worker-build`   | Build notification-worker only              |

### Test Commands

| Command                            | What it does                                |
|------------------------------------|---------------------------------------------|
| `make test`                        | Run all unit tests                          |
| `make payment-core-test`           | Run payment-core tests (Testify)            |
| `make key-management-test`         | Run key-management tests (JUnit)            |
| `make api-gateway-test`            | Run api-gateway tests (xUnit)               |
| `make notification-worker-test`    | Run notification-worker tests (Testify)     |
| `make integration-tests`           | Run Robot Framework integration tests       |
| `make perf-tests`                  | Run K6 performance tests (optional CI)      |

### Lint Commands

| Command                            | What it does                                |
|------------------------------------|---------------------------------------------|
| `make lint`                        | Lint all packages                           |
| `make payment-core-lint`           | golangci-lint on payment-core               |
| `make notification-worker-lint`    | golangci-lint on notification-worker        |
| `make key-management-lint`         | Checkstyle + SpotBugs on key-management     |
| `make api-gateway-lint`            | Roslyn analysers on api-gateway             |

### Full clean build from scratch

```bash
make all   # proto-gen → build → test → lint → integration-tests
```

## Package Overview

| Package                | Language            | Role                                           |
|------------------------|---------------------|------------------------------------------------|
| `proto/`               | Protobuf            | Shared .proto definitions + generated stubs    |
| `payment-core/`        | Go                  | Payment initiation, receiving, status tracking |
| `key-management/`      | Java (Spring Boot)  | PIX key CRUD + DICT stub integration           |
| `api-gateway/`         | C# (ASP.NET Core)   | REST → gRPC translation layer                  |
| `notification-worker/` | Go                  | Webhook event delivery + retry                 |
| `integration-tests/`   | Robot Framework     | End-to-end test suites                         |
| `perf-tests/`          | K6 (JavaScript)     | Load and latency tests                         |

## Local Port Assignments

| Service                | Port | Protocol                           |
|------------------------|------|------------------------------------|
| `api-gateway`          | 8080 | HTTP/REST (public entry point)     |
| `payment-core`         | 9090 | gRPC                               |
| `key-management`       | 9091 | gRPC                               |
| `notification-worker`  | —    | No inbound port (background worker)|

## Package Dependency Graph

```text
api-gateway (C#)
    ├── payment-core (gRPC :9090)
    └── key-management (gRPC :9091)

payment-core (Go)
    └── notification-worker (in-process event bus / Go channels)

notification-worker (Go)
    └── [outbound webhooks to registered URLs]

integration-tests
    └── api-gateway (:8080) — tests the full stack end-to-end

perf-tests
    └── api-gateway (:8080)
```

## Go Workspace

The Go modules (`payment-core` and `notification-worker`) are linked via `go.work` at the repo root.

```bash
go work sync   # re-sync after adding/removing Go modules
```

## Proto Generation

Proto stubs are committed to source control (in `proto/gen/`). Re-generate with:

```bash
make proto-gen
```

Requires: `protoc`, `protoc-gen-go`, `protoc-gen-go-grpc` (Go), `grpc_tools_dotnet_protoc` (C#), `protoc-gen-grpc-java` (Java).
