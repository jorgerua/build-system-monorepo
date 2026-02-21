// Package delivery handles webhook event delivery with retry and audit logging.
package delivery

import (
	"bytes"
	"context"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strings"
	"time"

	"github.com/example/pix/notification-worker/internal/store"
	"github.com/example/pix/payment-core/internal/eventbus"
)

// backoffSchedule defines the retry delays (exponential backoff: 1s,2s,4s,8s,16s).
var backoffSchedule = []time.Duration{
	1 * time.Second,
	2 * time.Second,
	4 * time.Second,
	8 * time.Second,
	16 * time.Second,
}

const deliveryTimeout = 5 * time.Second

// Worker consumes PaymentEvents from the bus and fans out to registered webhooks.
type Worker struct {
	db         *store.DB
	httpClient *http.Client
}

// New returns a Worker using the given store.
func New(db *store.DB) *Worker {
	return &Worker{
		db: db,
		httpClient: &http.Client{
			Timeout: deliveryTimeout,
			Transport: &http.Transport{
				TLSClientConfig: &tls.Config{MinVersion: tls.VersionTLS12},
			},
		},
	}
}

// Run subscribes to the bus and delivers events until ctx is cancelled.
func (w *Worker) Run(ctx context.Context, sub eventbus.Subscriber) {
	ch := sub.Subscribe()
	for {
		select {
		case <-ctx.Done():
			return
		case ev, ok := <-ch:
			if !ok {
				return
			}
			w.handleEvent(ctx, ev)
		}
	}
}

func (w *Worker) handleEvent(ctx context.Context, ev eventbus.PaymentEvent) {
	webhooks, err := w.db.GetWebhooksForEventType(ctx, ev.EventType)
	if err != nil {
		log.Printf("delivery: get webhooks for %s: %v", ev.EventType, err)
		return
	}
	for _, wh := range webhooks {
		go w.deliver(ctx, wh, ev)
	}
}

func (w *Worker) deliver(ctx context.Context, wh *store.Webhook, ev eventbus.PaymentEvent) {
	payload, err := json.Marshal(map[string]interface{}{
		"event_id":   ev.EventID,
		"event_type": ev.EventType,
		"payment_id": ev.PaymentID,
		"new_state":  ev.NewState,
		"occurred_at": ev.OccurredAt,
	})
	if err != nil {
		log.Printf("delivery: marshal payload: %v", err)
		return
	}

	for attempt := 1; attempt <= len(backoffSchedule)+1; attempt++ {
		httpStatus, err := w.post(wh.URL, payload)
		outcome := store.OutcomeDelivered
		if err != nil || httpStatus < 200 || httpStatus >= 300 {
			outcome = store.OutcomeFailed
			if attempt == len(backoffSchedule)+1 {
				outcome = store.OutcomePermanentlyFailed
			}
		}

		_ = w.db.LogDeliveryAttempt(ctx, wh.ID, ev.EventID, attempt, httpStatus, outcome)

		if outcome == store.OutcomeDelivered {
			return
		}
		if outcome == store.OutcomePermanentlyFailed {
			log.Printf("delivery: permanently failed for webhook %s event %s", wh.ID, ev.EventID)
			return
		}

		// Wait before retry
		delay := backoffSchedule[attempt-1]
		select {
		case <-ctx.Done():
			return
		case <-time.After(delay):
		}
	}
}

func (w *Worker) post(url string, payload []byte) (int, error) {
	if !strings.HasPrefix(url, "https://") {
		return 0, fmt.Errorf("non-HTTPS URL rejected: %s", url)
	}
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewReader(payload))
	if err != nil {
		return 0, err
	}
	req.Header.Set("Content-Type", "application/json")

	ctx, cancel := context.WithTimeout(context.Background(), deliveryTimeout)
	defer cancel()
	req = req.WithContext(ctx)

	resp, err := w.httpClient.Do(req)
	if err != nil {
		return 0, err
	}
	defer resp.Body.Close()
	return resp.StatusCode, nil
}

// ProbeURL checks that the URL is reachable (returns HTTP 2xx) before registration.
// Validates HTTPS-only.
func ProbeURL(url string) error {
	if !strings.HasPrefix(url, "https://") {
		return fmt.Errorf("INVALID_WEBHOOK_URL: only HTTPS URLs are accepted")
	}
	client := &http.Client{Timeout: 5 * time.Second}
	resp, err := client.Get(url)
	if err != nil {
		return fmt.Errorf("WEBHOOK_URL_UNREACHABLE: %w", err)
	}
	defer resp.Body.Close()
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return fmt.Errorf("WEBHOOK_URL_UNREACHABLE: probe returned HTTP %d", resp.StatusCode)
	}
	return nil
}
