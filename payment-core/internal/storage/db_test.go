package storage_test

import (
	"context"
	"testing"

	"github.com/example/pix/payment-core/internal/domain"
	"github.com/example/pix/payment-core/internal/storage"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func openTestDB(t *testing.T) *storage.DB {
	t.Helper()
	db, err := storage.Open(":memory:")
	require.NoError(t, err)
	t.Cleanup(func() { db.Close() })
	return db
}

func TestCreateAndGetPayment(t *testing.T) {
	db := openTestDB(t)
	ctx := context.Background()

	p, _ := domain.NewPayment("payer", "payee", 500, "key-1")
	require.NoError(t, db.CreatePayment(ctx, p))

	got, err := db.GetPaymentByID(ctx, p.ID)
	require.NoError(t, err)
	require.NotNil(t, got)
	assert.Equal(t, p.ID, got.ID)
	assert.Equal(t, domain.StateCreated, got.State)
}

func TestGetPaymentByIdempotencyKey(t *testing.T) {
	db := openTestDB(t)
	ctx := context.Background()

	p, _ := domain.NewPayment("a", "b", 1, "idem-unique")
	require.NoError(t, db.CreatePayment(ctx, p))

	got, err := db.GetPaymentByIdempotencyKey(ctx, "idem-unique")
	require.NoError(t, err)
	require.NotNil(t, got)
	assert.Equal(t, p.ID, got.ID)

	missing, err := db.GetPaymentByIdempotencyKey(ctx, "no-such-key")
	require.NoError(t, err)
	assert.Nil(t, missing)
}

func TestUpdatePaymentState(t *testing.T) {
	db := openTestDB(t)
	ctx := context.Background()

	p, _ := domain.NewPayment("a", "b", 1, "k")
	require.NoError(t, db.CreatePayment(ctx, p))

	prev := p.State
	require.NoError(t, p.Transition(domain.StateValidating))
	require.NoError(t, db.UpdatePaymentState(ctx, p, prev, ""))

	got, err := db.GetPaymentByID(ctx, p.ID)
	require.NoError(t, err)
	assert.Equal(t, domain.StateValidating, got.State)
}

func TestListPaymentsPage_FilterAndPagination(t *testing.T) {
	db := openTestDB(t)
	ctx := context.Background()

	for i := 0; i < 5; i++ {
		p, _ := domain.NewPayment("a", "b", int64(i+1), "k-"+string(rune('A'+i)))
		require.NoError(t, db.CreatePayment(ctx, p))
	}

	all, err := db.ListPaymentsPage(ctx, "", 0, 10)
	require.NoError(t, err)
	assert.Len(t, all, 5)

	page1, err := db.ListPaymentsPage(ctx, "", 0, 2)
	require.NoError(t, err)
	assert.Len(t, page1, 2)

	// No payments in SETTLED state
	settled, err := db.ListPaymentsPage(ctx, domain.StateSettled, 0, 10)
	require.NoError(t, err)
	assert.Empty(t, settled)
}
