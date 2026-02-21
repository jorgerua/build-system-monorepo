# Performance tests — K6
# Requires: k6 installed (https://k6.io/docs/getting-started/installation/)
# Services must be running before executing.

BASE_URL ?= http://localhost:8080

.PHONY: run run-create run-get run-lookup clean

run: run-create run-get run-lookup

run-create:
	@echo "==> K6: POST /payments throughput test"
	k6 run -e BASE_URL=$(BASE_URL) create_payment.js

run-get:
	@echo "==> K6: GET /payments/{id} latency test"
	k6 run -e BASE_URL=$(BASE_URL) get_payment.js

run-lookup:
	@echo "==> K6: GET /keys/{key} warm cache test"
	k6 run -e BASE_URL=$(BASE_URL) lookup_key.js
	@echo "==> K6: GET /keys/{key} cold cache test"
	k6 run -e BASE_URL=$(BASE_URL) -e SCENARIO=cold lookup_key.js

clean:
	rm -f *.json
