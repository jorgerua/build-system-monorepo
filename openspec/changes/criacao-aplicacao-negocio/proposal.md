## Why

The monorepo needs a concrete business application to validate the build system, CI/CD pipeline, and cross-language tooling. A PIX payment processing service — Brazil's mandatory instant payment system — provides a realistic, domain-rich workload spanning multiple languages and service boundaries.

## What Changes

- Introduce a new PIX payment processing service as the primary business application in the monorepo
- Implement payment initiation (send PIX) and receiving (receive PIX) flows
- Implement PIX key management (registration, lookup, and deletion of keys)
- Implement payment status tracking and event notifications (webhooks)
- Wire up unit, integration, and performance tests across the service stack

## Capabilities

### New Capabilities

- `payment-initiation`: Initiating outbound PIX payments — creating, validating, and submitting payment orders to the SPI (Sistema de Pagamentos Instantâneos)
- `payment-receiving`: Receiving inbound PIX payments — processing credits, validating payloads, and confirming settlement
- `key-management`: Managing PIX keys (CPF, CNPJ, e-mail, phone, random key) — registration, lookup, portability, and deletion via DICT
- `payment-status`: Querying and tracking the lifecycle state of payments (pending, processing, settled, failed, reversed)
- `notifications`: Emitting and delivering webhook events to consumers when payment state changes occur

### Modified Capabilities

<!-- No existing capabilities — this is a greenfield application -->

## Impact

- **New packages**: At least one service package per capability, organized under the monorepo workspace
- **Languages**: Go (core processing, performance-critical paths), C# (API layer / gateway), Java (integration and orchestration)
- **Test suites**: Unit tests (Testify / xUnit / JUnit), integration tests (Robot Framework), performance tests (K6)
- **Dependencies**: External PIX network (SPI / DICT) interfaces; internal message bus for async event flow
- **Build system**: All new packages must be integrated into the monorepo build, lint, and test pipelines
