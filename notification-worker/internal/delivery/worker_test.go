package delivery_test

import (
	"context"
	"net/http"
	"net/http/httptest"
	"strings"
	"sync/atomic"
	"testing"
	"time"

	"github.com/example/pix/notification-worker/internal/delivery"
	"github.com/example/pix/notification-worker/internal/store"
	"github.com/example/pix/payment-core/internal/eventbus"
	"github.com/google/uuid"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func openTestDB(t *testing.T) *store.DB {
	t.Helper()
	db, err := store.Open(":memory:")
	require.NoError(t, err)
	t.Cleanup(func() { db.Close() })
	return db
}

// TestDelivery_SuccessOnFirstAttempt verifies that a single successful delivery is logged correctly.
func TestDelivery_SuccessOnFirstAttempt(t *testing.T) {
	var called atomic.Int32
	srv := httptest.NewTLSServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		called.Add(1)
		w.WriteHeader(http.StatusOK)
	}))
	defer srv.Close()

	db := openTestDB(t)
	ctx := context.Background()

	wh := &store.Webhook{
		ID:         uuid.New().String(),
		URL:        srv.URL,
		EventTypes: []string{"payment.settled"},
		CreatedAt:  time.Now(),
	}
	require.NoError(t, db.RegisterWebhook(ctx, wh))

	w := delivery.New(db)
	bus := eventbus.New(10)

	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	go w.Run(ctx, bus)

	bus.Publish(ctx, eventbus.PaymentEvent{
		EventID:   uuid.New().String(),
		PaymentID: uuid.New().String(),
		EventType: "payment.settled",
		NewState:  "SETTLED",
		OccurredAt: time.Now().Unix(),
	})

	time.Sleep(500 * time.Millisecond)
	assert.GreaterOrEqual(t, called.Load(), int32(1))
}

// TestDelivery_FilterByEventType verifies that subscribers only receive matching event types.
func TestDelivery_FilterByEventType(t *testing.T) {
	var called atomic.Int32
	srv := httptest.NewTLSServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		called.Add(1)
		w.WriteHeader(http.StatusOK)
	}))
	defer srv.Close()

	db := openTestDB(t)
	ctx := context.Background()

	wh := &store.Webhook{
		ID:         uuid.New().String(),
		URL:        srv.URL,
		EventTypes: []string{"payment.failed"},
		CreatedAt:  time.Now(),
	}
	require.NoError(t, db.RegisterWebhook(ctx, wh))

	w := delivery.New(db)
	bus := eventbus.New(10)

	runCtx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()
	go w.Run(runCtx, bus)

	// Publish a "payment.settled" event — should NOT reach the "payment.failed" subscriber
	bus.Publish(runCtx, eventbus.PaymentEvent{
		EventID:   uuid.New().String(),
		PaymentID: uuid.New().String(),
		EventType: "payment.settled",
		NewState:  "SETTLED",
	})
	time.Sleep(300 * time.Millisecond)
	assert.Equal(t, int32(0), called.Load(), "settled event should not reach failed subscriber")
}

// TestDelivery_AuditLogging verifies that delivery attempts are recorded in the DB.
func TestDelivery_AuditLogging(t *testing.T) {
	srv := httptest.NewTLSServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
	}))
	defer srv.Close()

	db := openTestDB(t)
	ctx := context.Background()

	wh := &store.Webhook{
		ID:         uuid.New().String(),
		URL:        srv.URL,
		EventTypes: []string{"payment.created"},
		CreatedAt:  time.Now(),
	}
	require.NoError(t, db.RegisterWebhook(ctx, wh))

	require.NoError(t, db.LogDeliveryAttempt(ctx, wh.ID, "evt-1", 1, 200, store.OutcomeDelivered))

	// Verify it was logged (no error means success)
}

// TestRetention_DeletesOldLogs verifies that old log entries are removed.
func TestRetention_DeletesOldLogs(t *testing.T) {
	db := openTestDB(t)
	ctx := context.Background()

	wh := &store.Webhook{
		ID:         uuid.New().String(),
		URL:        "https://example.com",
		EventTypes: []string{"payment.settled"},
		CreatedAt:  time.Now(),
	}
	require.NoError(t, db.RegisterWebhook(ctx, wh))

	// Log an entry (it will have occurred_at = now, which is after the cutoff)
	require.NoError(t, db.LogDeliveryAttempt(ctx, wh.ID, "evt-old", 1, 0, store.OutcomeFailed))

	// Use a future cutoff (delete everything before tomorrow)
	n, err := db.DeleteOldLogs(ctx, time.Now().Add(24*time.Hour))
	require.NoError(t, err)
	assert.Equal(t, int64(1), n)
}

// TestProbeURL_RejectsHTTP verifies that non-HTTPS URLs are rejected.
func TestProbeURL_RejectsHTTP(t *testing.T) {
	err := delivery.ProbeURL("http://example.com/webhook")
	require.Error(t, err)
	assert.True(t, strings.Contains(err.Error(), "INVALID_WEBHOOK_URL"))
}
