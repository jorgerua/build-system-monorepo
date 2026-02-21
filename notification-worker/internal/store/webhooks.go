// Package store handles SQLite persistence for notification-worker.
package store

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"strings"
	"time"

	_ "github.com/mattn/go-sqlite3"
)

const schema = `
PRAGMA journal_mode=WAL;

CREATE TABLE IF NOT EXISTS webhooks (
    id          TEXT PRIMARY KEY,
    url         TEXT NOT NULL,
    event_types TEXT NOT NULL,
    created_at  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS delivery_log (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    webhook_id   TEXT NOT NULL,
    event_id     TEXT NOT NULL,
    attempt      INTEGER NOT NULL,
    occurred_at  INTEGER NOT NULL,
    http_status  INTEGER NOT NULL DEFAULT 0,
    outcome      TEXT NOT NULL,
    FOREIGN KEY (webhook_id) REFERENCES webhooks(id)
);

CREATE INDEX IF NOT EXISTS idx_delivery_log_webhook ON delivery_log(webhook_id);
CREATE INDEX IF NOT EXISTS idx_delivery_log_occurred ON delivery_log(occurred_at);
`

// Webhook is a registered webhook endpoint.
type Webhook struct {
	ID         string
	URL        string
	EventTypes []string
	CreatedAt  time.Time
}

// DeliveryOutcome records the outcome of a single delivery attempt.
type DeliveryOutcome string

const (
	OutcomeDelivered        DeliveryOutcome = "DELIVERED"
	OutcomeFailed           DeliveryOutcome = "FAILED"
	OutcomePermanentlyFailed DeliveryOutcome = "PERMANENTLY_FAILED"
)

// DB wraps a SQLite connection for notification-worker persistence.
type DB struct {
	db *sql.DB
}

// Open opens or creates the SQLite database and applies the schema.
func Open(dsn string) (*DB, error) {
	db, err := sql.Open("sqlite3", dsn)
	if err != nil {
		return nil, fmt.Errorf("open db: %w", err)
	}
	db.SetMaxOpenConns(1)
	if _, err := db.Exec(schema); err != nil {
		return nil, fmt.Errorf("apply schema: %w", err)
	}
	return &DB{db: db}, nil
}

// Close closes the underlying connection.
func (s *DB) Close() error { return s.db.Close() }

// RegisterWebhook persists a new webhook registration.
func (s *DB) RegisterWebhook(ctx context.Context, wh *Webhook) error {
	_, err := s.db.ExecContext(ctx,
		`INSERT INTO webhooks (id, url, event_types, created_at) VALUES (?,?,?,?)`,
		wh.ID, wh.URL, strings.Join(wh.EventTypes, ","), wh.CreatedAt.Unix(),
	)
	return err
}

// DeleteWebhook removes a webhook by ID.
func (s *DB) DeleteWebhook(ctx context.Context, id string) error {
	res, err := s.db.ExecContext(ctx, `DELETE FROM webhooks WHERE id = ?`, id)
	if err != nil {
		return err
	}
	n, _ := res.RowsAffected()
	if n == 0 {
		return errors.New("webhook not found")
	}
	return nil
}

// GetWebhooksForEventType returns all webhooks subscribed to the given event type.
func (s *DB) GetWebhooksForEventType(ctx context.Context, eventType string) ([]*Webhook, error) {
	rows, err := s.db.QueryContext(ctx,
		`SELECT id, url, event_types, created_at FROM webhooks`)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []*Webhook
	for rows.Next() {
		var wh Webhook
		var eventTypes string
		var createdAt int64
		if err := rows.Scan(&wh.ID, &wh.URL, &eventTypes, &createdAt); err != nil {
			return nil, err
		}
		wh.EventTypes = strings.Split(eventTypes, ",")
		wh.CreatedAt = time.Unix(createdAt, 0).UTC()
		for _, et := range wh.EventTypes {
			if et == eventType {
				result = append(result, &wh)
				break
			}
		}
	}
	return result, rows.Err()
}

// LogDeliveryAttempt records a webhook delivery attempt.
func (s *DB) LogDeliveryAttempt(ctx context.Context, webhookID, eventID string, attempt int, httpStatus int, outcome DeliveryOutcome) error {
	_, err := s.db.ExecContext(ctx,
		`INSERT INTO delivery_log (webhook_id, event_id, attempt, occurred_at, http_status, outcome)
		 VALUES (?,?,?,?,?,?)`,
		webhookID, eventID, attempt, time.Now().Unix(), httpStatus, string(outcome),
	)
	return err
}

// DeleteOldLogs deletes delivery log entries older than the given cutoff time.
func (s *DB) DeleteOldLogs(ctx context.Context, cutoff time.Time) (int64, error) {
	res, err := s.db.ExecContext(ctx,
		`DELETE FROM delivery_log WHERE occurred_at < ?`, cutoff.Unix())
	if err != nil {
		return 0, err
	}
	return res.RowsAffected()
}
