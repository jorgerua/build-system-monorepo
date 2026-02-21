// Command server starts the payment-core gRPC service.
package main

import (
	"fmt"
	"log"
	"net"
	"os"

	"github.com/example/pix/payment-core/internal/eventbus"
	"github.com/example/pix/payment-core/internal/grpcserver"
	"github.com/example/pix/payment-core/internal/spi"
	"github.com/example/pix/payment-core/internal/storage"
	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"

	paymentv1 "github.com/example/pix/payment-core/gen/payment/v1"
)

func main() {
	port := os.Getenv("PAYMENT_CORE_PORT")
	if port == "" {
		port = "9090"
	}
	dbPath := os.Getenv("PAYMENT_CORE_DB")
	if dbPath == "" {
		dbPath = "payment-core.db"
	}

	db, err := storage.Open(dbPath)
	if err != nil {
		log.Fatalf("open db: %v", err)
	}
	defer db.Close()

	bus := eventbus.New(256)
	spiClient := spi.NewStubClient()
	srv := grpcserver.New(db, spiClient, bus)

	lis, err := net.Listen("tcp", fmt.Sprintf(":%s", port))
	if err != nil {
		log.Fatalf("listen: %v", err)
	}

	grpcSrv := grpc.NewServer()
	paymentv1.RegisterPaymentServiceServer(grpcSrv, srv)
	reflection.Register(grpcSrv)

	log.Printf("payment-core gRPC server listening on :%s", port)
	if err := grpcSrv.Serve(lis); err != nil {
		log.Fatalf("serve: %v", err)
	}
}
