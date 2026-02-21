package domain

import (
	"errors"
	"time"

	"github.com/google/uuid"
)

const (
	MinAmountCentavos int64 = 1             // R$ 0.01
	MaxAmountCentavos int64 = 99_999_999_999 // R$ 999,999,999.99
)

// Payment is the core aggregate.
type Payment struct {
	ID              string
	State           PaymentState
	PayerKey        string
	PayeeKey        string
	AmountCentavos  int64
	IdempotencyKey  string
	SPIEndToEndID   string
	RejectionReason string
	CreatedAt       time.Time
	UpdatedAt       time.Time
}

// NewPayment creates a Payment in CREATED state after validating inputs.
func NewPayment(payerKey, payeeKey string, amountCentavos int64, idempotencyKey string) (*Payment, error) {
	if payerKey == "" {
		return nil, errors.New("MISSING_FIELD: payer_key is required")
	}
	if payeeKey == "" {
		return nil, errors.New("MISSING_FIELD: payee_key is required")
	}
	if amountCentavos < MinAmountCentavos {
		return nil, errors.New("INVALID_AMOUNT: amount must be at least R$ 0.01")
	}
	if amountCentavos > MaxAmountCentavos {
		return nil, errors.New("AMOUNT_EXCEEDS_LIMIT: amount exceeds R$ 999,999,999.99")
	}
	now := time.Now().UTC()
	return &Payment{
		ID:             uuid.New().String(),
		State:          StateCreated,
		PayerKey:       payerKey,
		PayeeKey:       payeeKey,
		AmountCentavos: amountCentavos,
		IdempotencyKey: idempotencyKey,
		CreatedAt:      now,
		UpdatedAt:      now,
	}, nil
}

// Transition moves the payment to the next state, returning an error if invalid.
func (p *Payment) Transition(to PaymentState) error {
	if err := ValidateTransition(p.State, to); err != nil {
		return err
	}
	p.State = to
	p.UpdatedAt = time.Now().UTC()
	return nil
}
