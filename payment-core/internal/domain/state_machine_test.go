package domain_test

import (
	"testing"

	"github.com/example/pix/payment-core/internal/domain"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestValidTransitions(t *testing.T) {
	cases := []struct {
		from domain.PaymentState
		to   domain.PaymentState
	}{
		{domain.StateCreated, domain.StateValidating},
		{domain.StateValidating, domain.StateSubmitted},
		{domain.StateValidating, domain.StateFailed},
		{domain.StateSubmitted, domain.StateSettled},
		{domain.StateSubmitted, domain.StateFailed},
		{domain.StateSettled, domain.StateReversing},
		{domain.StateReversing, domain.StateReversed},
	}
	for _, tc := range cases {
		t.Run(string(tc.from)+"->"+string(tc.to), func(t *testing.T) {
			require.NoError(t, domain.ValidateTransition(tc.from, tc.to))
		})
	}
}

func TestInvalidTransitions(t *testing.T) {
	cases := []struct {
		from domain.PaymentState
		to   domain.PaymentState
	}{
		{domain.StateCreated, domain.StateSettled},
		{domain.StateCreated, domain.StateFailed},
		{domain.StateSettled, domain.StateCreated},
		{domain.StateFailed, domain.StateSubmitted},
		{domain.StateReversed, domain.StateReversing},
	}
	for _, tc := range cases {
		t.Run(string(tc.from)+"->"+string(tc.to), func(t *testing.T) {
			err := domain.ValidateTransition(tc.from, tc.to)
			require.Error(t, err)
			assert.Contains(t, err.Error(), "invalid state transition")
		})
	}
}

func TestTerminalStates(t *testing.T) {
	assert.True(t, domain.IsTerminal(domain.StateSettled))
	assert.True(t, domain.IsTerminal(domain.StateFailed))
	assert.True(t, domain.IsTerminal(domain.StateReversed))
	assert.False(t, domain.IsTerminal(domain.StateCreated))
	assert.False(t, domain.IsTerminal(domain.StateSubmitted))
}
