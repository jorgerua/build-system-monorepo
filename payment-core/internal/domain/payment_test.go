package domain_test

import (
	"testing"

	"github.com/example/pix/payment-core/internal/domain"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestNewPayment_Valid(t *testing.T) {
	p, err := domain.NewPayment("key-payer", "key-payee", 100, "idem-1")
	require.NoError(t, err)
	assert.Equal(t, domain.StateCreated, p.State)
	assert.Equal(t, int64(100), p.AmountCentavos)
	assert.NotEmpty(t, p.ID)
}

func TestNewPayment_AmountBoundaries(t *testing.T) {
	_, err := domain.NewPayment("a", "b", 0, "k")
	assert.Error(t, err, "zero amount should fail")

	_, err = domain.NewPayment("a", "b", -1, "k")
	assert.Error(t, err, "negative amount should fail")

	_, err = domain.NewPayment("a", "b", domain.MaxAmountCentavos+1, "k")
	assert.Error(t, err, "amount above max should fail")

	p, err := domain.NewPayment("a", "b", domain.MinAmountCentavos, "k")
	require.NoError(t, err)
	assert.Equal(t, domain.MinAmountCentavos, p.AmountCentavos)

	p, err = domain.NewPayment("a", "b", domain.MaxAmountCentavos, "k")
	require.NoError(t, err)
	assert.Equal(t, domain.MaxAmountCentavos, p.AmountCentavos)
}

func TestNewPayment_MissingFields(t *testing.T) {
	_, err := domain.NewPayment("", "payee", 100, "k")
	require.Error(t, err)
	assert.Contains(t, err.Error(), "MISSING_FIELD")

	_, err = domain.NewPayment("payer", "", 100, "k")
	require.Error(t, err)
	assert.Contains(t, err.Error(), "MISSING_FIELD")
}

func TestPayment_Transition(t *testing.T) {
	p, _ := domain.NewPayment("a", "b", 100, "k")
	require.NoError(t, p.Transition(domain.StateValidating))
	assert.Equal(t, domain.StateValidating, p.State)
}

func TestPayment_InvalidTransition(t *testing.T) {
	p, _ := domain.NewPayment("a", "b", 100, "k")
	err := p.Transition(domain.StateSettled)
	require.Error(t, err)
	assert.Equal(t, domain.StateCreated, p.State, "state must not change on failed transition")
}
