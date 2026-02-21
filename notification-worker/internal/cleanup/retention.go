// Package cleanup provides the 7-day audit log retention job.
package cleanup

import (
	"context"
	"log"
	"time"

	"github.com/example/pix/notification-worker/internal/store"
)

const retentionPeriod = 7 * 24 * time.Hour

// RunRetentionJob deletes delivery log entries older than 7 days.
// It runs once immediately, then on the given interval until ctx is cancelled.
func RunRetentionJob(ctx context.Context, db *store.DB, interval time.Duration) {
	tick := time.NewTicker(interval)
	defer tick.Stop()

	runOnce(ctx, db)

	for {
		select {
		case <-ctx.Done():
			return
		case <-tick.C:
			runOnce(ctx, db)
		}
	}
}

func runOnce(ctx context.Context, db *store.DB) {
	cutoff := time.Now().UTC().Add(-retentionPeriod)
	n, err := db.DeleteOldLogs(ctx, cutoff)
	if err != nil {
		log.Printf("retention: delete old logs: %v", err)
		return
	}
	if n > 0 {
		log.Printf("retention: deleted %d audit log entries older than %s", n, cutoff.Format(time.RFC3339))
	}
}
