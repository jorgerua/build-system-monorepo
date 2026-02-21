# Root Makefile — PIX Payment Processing Monorepo
# Build tool: GNU Make (chosen for universal language support — Go, Java, C#, Robot Framework, K6)
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

# ── Proto generation ──────────────────────────────────────────────────────────
proto-gen:
	@echo "==> Generating proto stubs..."
	$(MAKE) -C proto all

# ── payment-core (Go) ─────────────────────────────────────────────────────────
payment-core-build: proto-gen
	@echo "==> Building payment-core..."
	cd payment-core && go build ./...

payment-core-test: proto-gen
	@echo "==> Testing payment-core..."
	cd payment-core && go test ./...

payment-core-lint:
	@echo "==> Linting payment-core..."
	cd payment-core && golangci-lint run ./...

# ── notification-worker (Go) ──────────────────────────────────────────────────
notification-worker-build: proto-gen
	@echo "==> Building notification-worker..."
	cd notification-worker && go build ./...

notification-worker-test: proto-gen
	@echo "==> Testing notification-worker..."
	cd notification-worker && go test ./...

notification-worker-lint:
	@echo "==> Linting notification-worker..."
	cd notification-worker && golangci-lint run ./...

# ── key-management (Java/Gradle) ─────────────────────────────────────────────
key-management-build: proto-gen
	@echo "==> Building key-management..."
	cd key-management && ./gradlew build -x test

key-management-test: proto-gen
	@echo "==> Testing key-management..."
	cd key-management && ./gradlew test

key-management-lint:
	@echo "==> Linting key-management..."
	cd key-management && ./gradlew checkstyleMain spotbugsMain

# ── api-gateway (C# / .NET) ──────────────────────────────────────────────────
api-gateway-build: proto-gen
	@echo "==> Building api-gateway..."
	cd api-gateway && dotnet build

api-gateway-test: proto-gen
	@echo "==> Testing api-gateway..."
	cd api-gateway && dotnet test

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

# ── Aggregate targets ─────────────────────────────────────────────────────────
build: payment-core-build notification-worker-build key-management-build api-gateway-build

test: payment-core-test notification-worker-test key-management-test api-gateway-test

lint: payment-core-lint notification-worker-lint key-management-lint api-gateway-lint

all: proto-gen build test lint integration-tests

clean:
	cd payment-core        && go clean ./...
	cd notification-worker && go clean ./...
	cd key-management      && ./gradlew clean
	cd api-gateway         && dotnet clean
