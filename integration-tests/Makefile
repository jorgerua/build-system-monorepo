# Integration tests — Robot Framework
# Requires: pip install -r requirements.txt
# Services must be running before executing: api-gateway(:8080), payment-core(:9090), key-management(:9091)

.PHONY: install run clean

install:
	pip install -r requirements.txt

run:
	robot --outputdir results suites/

clean:
	rm -rf results/
