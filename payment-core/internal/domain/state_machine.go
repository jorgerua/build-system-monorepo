// Package domain contains the core payment domain logic.
package domain

import "fmt"

// PaymentState represents the lifecycle state of a payment.
type PaymentState string

const (
	StateCreated    PaymentState = "CREATED"
	StateValidating PaymentState = "VALIDATING"
	StateSubmitted  PaymentState = "SUBMITTED"
	StateSettled    PaymentState = "SETTLED"
	StateFailed     PaymentState = "FAILED"
	StateReversing  PaymentState = "REVERSING"
	StateReversed   PaymentState = "REVERSED"
)

// validTransitions defines the only allowed state machine moves.
var validTransitions = map[PaymentState][]PaymentState{
	StateCreated:    {StateValidating},
	StateValidating: {StateSubmitted, StateFailed},
	StateSubmitted:  {StateSettled, StateFailed},
	StateSettled:    {StateReversing},
	StateReversing:  {StateReversed},
	StateFailed:     {},
	StateReversed:   {},
}

// ErrInvalidStateTransition is returned when a transition is not permitted.
type ErrInvalidStateTransition struct {
	From PaymentState
	To   PaymentState
}

func (e *ErrInvalidStateTransition) Error() string {
	return fmt.Sprintf("invalid state transition: %s → %s", e.From, e.To)
}

// ValidateTransition returns an error if transitioning from `from` to `to` is not permitted.
func ValidateTransition(from, to PaymentState) error {
	allowed, ok := validTransitions[from]
	if !ok {
		return &ErrInvalidStateTransition{From: from, To: to}
	}
	for _, s := range allowed {
		if s == to {
			return nil
		}
	}
	return &ErrInvalidStateTransition{From: from, To: to}
}

// IsTerminal reports whether s is a terminal state (no further transitions).
func IsTerminal(s PaymentState) bool {
	return s == StateSettled || s == StateFailed || s == StateReversed
}
