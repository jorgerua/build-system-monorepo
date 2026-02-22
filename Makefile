# Root Makefile — PIX Payment Processing Monorepo
# Build tool: GNU Make + Nx (chosen for universal language support — Go, Java, C#, Robot Framework, K6)
# Execution order: proto-gen → packages → tests

.PHONY: all proto-gen \
        payment-core-build payment-core-test payment-core-lint \
        key-management-build key-management-test key-management-lint \
        api-gateway-build api-gateway-test api-gateway-lint \
        notification-worker-build notification-worker-test notification-worker-lint \
        integration-tests perf-tests \
        build test lint clean

# ── Ports (see CLAUDE.md for full reference) ─────────────────────────────────
API_GATEWAY_PORT        ?= 8080
PAYMENT_CORE_GRPC_PORT  ?= 9090
KEY_MGMT_GRPC_PORT      ?= 9091
# notification-worker is a background worker; no inbound port

# ── Nx wrapper ────────────────────────────────────────────────────────────────
NX ?= ./nx

# ── Proto generation ──────────────────────────────────────────────────────────
proto-gen:
	@echo "==> Generating proto stubs..."
	$(MAKE) -C proto all

# ── Aggregate build (all packages via Nx, parallel) ──────────────────────────
build: proto-gen
	@echo "==> Building all packages via Nx..."
	$(NX) run-many -t build --parallel=4

# ── Aggregate test (all packages via Nx, parallel) ───────────────────────────
test: proto-gen
	@echo "==> Running unit tests for all packages via Nx..."
	$(NX) run-many -t test --parallel=4

# ── payment-core (Go) ─────────────────────────────────────────────────────────
payment-core-build: proto-gen
	@echo "==> Building payment-core..."
	$(NX) run payment-core:build

payment-core-test: proto-gen
	@echo "==> Testing payment-core..."
	$(NX) run payment-core:test

payment-core-lint:
	@echo "==> Linting payment-core..."
	cd payment-core && golangci-lint run ./...

# ── notification-worker (Go) ──────────────────────────────────────────────────
notification-worker-build: proto-gen
	@echo "==> Building notification-worker..."
	$(NX) run notification-worker:build

notification-worker-test: proto-gen
	@echo "==> Testing notification-worker..."
	$(NX) run notification-worker:test

notification-worker-lint:
	@echo "==> Linting notification-worker..."
	cd notification-worker && golangci-lint run ./...

# ── key-management (Java/Gradle) ─────────────────────────────────────────────
key-management-build: proto-gen
	@echo "==> Building key-management..."
	$(NX) run key-management:build

key-management-test: proto-gen
	@echo "==> Testing key-management..."
	$(NX) run key-management:test

key-management-lint:
	@echo "==> Linting key-management..."
	cd key-management && ./gradlew checkstyleMain spotbugsMain

# ── api-gateway (C# / .NET) ──────────────────────────────────────────────────
api-gateway-build: proto-gen
	@echo "==> Building api-gateway..."
	$(NX) run api-gateway:build

api-gateway-test: proto-gen
	@echo "==> Testing api-gateway..."
	$(NX) run api-gateway:test

api-gateway-lint:
	@echo "==> Linting api-gateway..."
	cd api-gateway && dotnet build /p:TreatWarningsAsErrors=true

# ── Integration tests (Robot Framework) ──────────────────────────────────────
integration-tests:
	@echo "==> Running integration tests..."
	$(MAKE) -C integration-tests run

# ── Performance tests (K6) ───────────────────────────────────────────────────
perf-tests:
	@echo "==> Running performance tests (optional)..."
	$(MAKE) -C perf-tests run

# ── Lint aggregate ────────────────────────────────────────────────────────────
lint: payment-core-lint notification-worker-lint key-management-lint api-gateway-lint

# ── Full pipeline ─────────────────────────────────────────────────────────────
all: proto-gen build test lint integration-tests

clean:
	cd payment-core        && go clean ./...
	cd notification-worker && go clean ./...
	cd key-management      && ./gradlew clean
	cd api-gateway         && dotnet clean
