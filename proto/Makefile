# proto/Makefile — generates Go, C#, and Java gRPC stubs
# Called by root Makefile via: $(MAKE) -C proto all

PROTO_DIR   := $(CURDIR)
GO_OUT      := $(PROTO_DIR)/gen/go
CSHARP_OUT  := $(PROTO_DIR)/gen/csharp
JAVA_OUT    := $(PROTO_DIR)/gen/java

PROTO_FILES := payment.proto keys.proto events.proto

.PHONY: all clean go-stubs csharp-stubs java-stubs

all: go-stubs csharp-stubs java-stubs

go-stubs: $(PROTO_FILES)
	@mkdir -p $(GO_OUT)
	protoc \
		--proto_path=$(PROTO_DIR) \
		--go_out=$(GO_OUT) \
		--go_opt=paths=source_relative \
		--go-grpc_out=$(GO_OUT) \
		--go-grpc_opt=paths=source_relative \
		$(PROTO_FILES)

csharp-stubs: $(PROTO_FILES)
	@mkdir -p $(CSHARP_OUT)
	protoc \
		--proto_path=$(PROTO_DIR) \
		--csharp_out=$(CSHARP_OUT) \
		--grpc_out=$(CSHARP_OUT) \
		--plugin=protoc-gen-grpc=$(shell which grpc_csharp_plugin) \
		$(PROTO_FILES)

java-stubs: $(PROTO_FILES)
	@mkdir -p $(JAVA_OUT)
	protoc \
		--proto_path=$(PROTO_DIR) \
		--java_out=$(JAVA_OUT) \
		--grpc-java_out=$(JAVA_OUT) \
		--plugin=protoc-gen-grpc-java=$(shell which protoc-gen-grpc-java) \
		$(PROTO_FILES)

clean:
	rm -rf $(GO_OUT) $(CSHARP_OUT) $(JAVA_OUT)
