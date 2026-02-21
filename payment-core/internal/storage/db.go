// Package storage handles SQLite persistence for payment-core.
package storage

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"time"

	"github.com/example/pix/payment-core/internal/domain"
	_ "github.com/mattn/go-sqlite3"
)

const schema = `
PRAGMA journal_mode=WAL;

CREATE TABLE IF NOT EXISTS payments (
    id               TEXT PRIMARY KEY,
    state            TEXT NOT NULL,
    payer_key        TEXT NOT NULL,
    payee_key        TEXT NOT NULL,
    amount_centavos  INTEGER NOT NULL,
    idempotency_key  TEXT NOT NULL UNIQUE,
    spi_end_to_end   TEXT NOT NULL DEFAULT '',
    rejection_reason TEXT NOT NULL DEFAULT '',
    created_at       INTEGER NOT NULL,
    updated_at       INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS payment_events (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    payment_id     TEXT NOT NULL,
    previous_state TEXT NOT NULL,
    new_state      TEXT NOT NULL,
    reason_code    TEXT NOT NULL DEFAULT '',
    occurred_at    INTEGER NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE INDEX IF NOT EXISTS idx_payments_idempotency ON payments(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_payments_state       ON payments(state, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_events_payment       ON payment_events(payment_id);
`

// DB wraps a SQLite connection and provides payment persistence.
type DB struct {
	db *sql.DB
}

// Open opens (or creates) the SQLite database at dsn and applies the schema.
func Open(dsn string) (*DB, error) {
	db, err := sql.Open("sqlite3", dsn)
	if err != nil {
		return nil, fmt.Errorf("open db: %w", err)
	}
	db.SetMaxOpenConns(1) // SQLite write serialisation
	if _, err := db.Exec(schema); err != nil {
		return nil, fmt.Errorf("apply schema: %w", err)
	}
	return &DB{db: db}, nil
}

// Close closes the underlying connection.
func (s *DB) Close() error { return s.db.Close() }

// CreatePayment persists a new payment record.
func (s *DB) CreatePayment(ctx context.Context, p *domain.Payment) error {
	_, err := s.db.ExecContext(ctx,
		`INSERT INTO payments
		 (id, state, payer_key, payee_key, amount_centavos, idempotency_key,
		  spi_end_to_end, rejection_reason, created_at, updated_at)
		 VALUES (?,?,?,?,?,?,?,?,?,?)`,
		p.ID, string(p.State), p.PayerKey, p.PayeeKey, p.AmountCentavos,
		p.IdempotencyKey, p.SPIEndToEndID, p.RejectionReason,
		p.CreatedAt.Unix(), p.UpdatedAt.Unix(),
	)
	return err
}

// GetPaymentByID retrieves a payment by its internal ID.
func (s *DB) GetPaymentByID(ctx context.Context, id string) (*domain.Payment, error) {
	row := s.db.QueryRowContext(ctx,
		`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
		        spi_end_to_end, rejection_reason, created_at, updated_at
		 FROM payments WHERE id = ?`, id)
	return scanPayment(row)
}

// GetPaymentByIdempotencyKey retrieves a payment by idempotency key (for deduplication).
func (s *DB) GetPaymentByIdempotencyKey(ctx context.Context, key string) (*domain.Payment, error) {
	row := s.db.QueryRowContext(ctx,
		`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
		        spi_end_to_end, rejection_reason, created_at, updated_at
		 FROM payments WHERE idempotency_key = ?`, key)
	return scanPayment(row)
}

// GetPaymentBySPIEndToEndID retrieves a payment by SPI end-to-end ID (inbound idempotency).
func (s *DB) GetPaymentBySPIEndToEndID(ctx context.Context, endToEndID string) (*domain.Payment, error) {
	row := s.db.QueryRowContext(ctx,
		`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
		        spi_end_to_end, rejection_reason, created_at, updated_at
		 FROM payments WHERE spi_end_to_end = ?`, endToEndID)
	return scanPayment(row)
}

// ListPaymentsPage returns a page of payments filtered by state, ordered by created_at DESC.
// cursor is the created_at Unix timestamp to start after (exclusive); 0 means first page.
func (s *DB) ListPaymentsPage(ctx context.Context, stateFilter domain.PaymentState, cursor int64, pageSize int) ([]*domain.Payment, error) {
	if pageSize <= 0 || pageSize > 100 {
		pageSize = 20
	}

	var rows *sql.Rows
	var err error

	if stateFilter == "" {
		if cursor == 0 {
			rows, err = s.db.QueryContext(ctx,
				`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
				        spi_end_to_end, rejection_reason, created_at, updated_at
				 FROM payments ORDER BY created_at DESC LIMIT ?`, pageSize)
		} else {
			rows, err = s.db.QueryContext(ctx,
				`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
				        spi_end_to_end, rejection_reason, created_at, updated_at
				 FROM payments WHERE created_at < ? ORDER BY created_at DESC LIMIT ?`, cursor, pageSize)
		}
	} else {
		if cursor == 0 {
			rows, err = s.db.QueryContext(ctx,
				`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
				        spi_end_to_end, rejection_reason, created_at, updated_at
				 FROM payments WHERE state = ? ORDER BY created_at DESC LIMIT ?`, string(stateFilter), pageSize)
		} else {
			rows, err = s.db.QueryContext(ctx,
				`SELECT id, state, payer_key, payee_key, amount_centavos, idempotency_key,
				        spi_end_to_end, rejection_reason, created_at, updated_at
				 FROM payments WHERE state = ? AND created_at < ? ORDER BY created_at DESC LIMIT ?`,
				string(stateFilter), cursor, pageSize)
		}
	}
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var payments []*domain.Payment
	for rows.Next() {
		p, err := scanPaymentRow(rows)
		if err != nil {
			return nil, err
		}
		payments = append(payments, p)
	}
	return payments, rows.Err()
}

// UpdatePaymentState persists a state transition and appends an event log entry.
func (s *DB) UpdatePaymentState(ctx context.Context, p *domain.Payment, prevState domain.PaymentState, reasonCode string) error {
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback() //nolint:errcheck

	_, err = tx.ExecContext(ctx,
		`UPDATE payments SET state=?, spi_end_to_end=?, rejection_reason=?, updated_at=? WHERE id=?`,
		string(p.State), p.SPIEndToEndID, p.RejectionReason, p.UpdatedAt.Unix(), p.ID,
	)
	if err != nil {
		return err
	}

	_, err = tx.ExecContext(ctx,
		`INSERT INTO payment_events (payment_id, previous_state, new_state, reason_code, occurred_at)
		 VALUES (?,?,?,?,?)`,
		p.ID, string(prevState), string(p.State), reasonCode, time.Now().Unix(),
	)
	if err != nil {
		return err
	}

	return tx.Commit()
}

func scanPayment(row *sql.Row) (*domain.Payment, error) {
	var p domain.Payment
	var createdAt, updatedAt int64
	var state string
	err := row.Scan(
		&p.ID, &state, &p.PayerKey, &p.PayeeKey, &p.AmountCentavos, &p.IdempotencyKey,
		&p.SPIEndToEndID, &p.RejectionReason, &createdAt, &updatedAt,
	)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	p.State = domain.PaymentState(state)
	p.CreatedAt = time.Unix(createdAt, 0).UTC()
	p.UpdatedAt = time.Unix(updatedAt, 0).UTC()
	return &p, nil
}

func scanPaymentRow(rows *sql.Rows) (*domain.Payment, error) {
	var p domain.Payment
	var createdAt, updatedAt int64
	var state string
	err := rows.Scan(
		&p.ID, &state, &p.PayerKey, &p.PayeeKey, &p.AmountCentavos, &p.IdempotencyKey,
		&p.SPIEndToEndID, &p.RejectionReason, &createdAt, &updatedAt,
	)
	if err != nil {
		return nil, err
	}
	p.State = domain.PaymentState(state)
	p.CreatedAt = time.Unix(createdAt, 0).UTC()
	p.UpdatedAt = time.Unix(updatedAt, 0).UTC()
	return &p, nil
}
