// Command server starts the notification-worker service.
// It subscribes to the payment-core event bus and delivers webhooks to registered URLs.
package main

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/example/pix/notification-worker/internal/cleanup"
	"github.com/example/pix/notification-worker/internal/delivery"
	"github.com/example/pix/notification-worker/internal/store"
	"github.com/example/pix/payment-core/internal/eventbus"
)

func main() {
	dbPath := os.Getenv("NOTIFICATION_WORKER_DB")
	if dbPath == "" {
		dbPath = "notification-worker.db"
	}

	db, err := store.Open(dbPath)
	if err != nil {
		log.Fatalf("open db: %v", err)
	}
	defer db.Close()

	// In production the bus would be provided by payment-core (same process or via IPC).
	// For standalone testing, we create a local bus.
	bus := eventbus.New(256)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	worker := delivery.New(db)
	go worker.Run(ctx, bus)

	go cleanup.RunRetentionJob(ctx, db, 24*time.Hour)

	log.Println("notification-worker started (no inbound port — background worker)")

	sig := make(chan os.Signal, 1)
	signal.Notify(sig, syscall.SIGINT, syscall.SIGTERM)
	<-sig
	log.Println("notification-worker shutting down")
}
