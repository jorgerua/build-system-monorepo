// Package spi provides a stub SPI (Sistema de Pagamentos Instantâneos) client.
// Clearly labelled as a test double — real SPI connectivity is out of scope.
package spi

import (
	"context"
	"fmt"

	"github.com/google/uuid"
)

// SubmitResult is the outcome of a SPI submission.
type SubmitResult struct {
	EndToEndID     string
	RejectionCode  string
	Accepted       bool
}

// Client is the interface for submitting payments to SPI.
type Client interface {
	SubmitPayment(ctx context.Context, paymentID, payerKey, payeeKey string, amountCentavos int64) (*SubmitResult, error)
}

// StubClient is a test double that always accepts submissions.
// To simulate rejection, set RejectNextN > 0.
type StubClient struct {
	RejectNextN int
	RejectCode  string
}

// NewStubClient returns a new StubClient (always accepts).
func NewStubClient() *StubClient {
	return &StubClient{}
}

// SubmitPayment simulates SPI submission.
func (c *StubClient) SubmitPayment(_ context.Context, paymentID, _, _ string, _ int64) (*SubmitResult, error) {
	if c.RejectNextN > 0 {
		c.RejectNextN--
		code := c.RejectCode
		if code == "" {
			code = "SPI_REJECTION_INVALID_ACCOUNT"
		}
		return &SubmitResult{Accepted: false, RejectionCode: code}, nil
	}
	endToEndID := fmt.Sprintf("E%s%s", paymentID[:8], uuid.New().String()[:8])
	return &SubmitResult{Accepted: true, EndToEndID: endToEndID}, nil
}
