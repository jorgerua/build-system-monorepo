// Package eventbus provides an in-process event bus for payment domain events.
// The bus is backed by a Go channel and can be swapped for an external broker
// without changing production service code.
package eventbus

import "context"

// PaymentEvent carries state-transition information published on each payment change.
type PaymentEvent struct {
	EventID       string
	PaymentID     string
	EventType     string // e.g. "payment.settled"
	PreviousState string
	NewState      string
	OccurredAt    int64  // Unix timestamp
	ReasonCode    string // optional, set on FAILED / REVERSED
}

// Publisher allows domain code to publish events without depending on the bus implementation.
type Publisher interface {
	Publish(ctx context.Context, event PaymentEvent)
}

// Subscriber allows workers to consume events from the bus.
type Subscriber interface {
	Subscribe() <-chan PaymentEvent
}

// Bus is the in-process event bus implementation.
type Bus struct {
	ch chan PaymentEvent
}

// New creates a Bus with the given channel buffer size.
func New(bufferSize int) *Bus {
	return &Bus{ch: make(chan PaymentEvent, bufferSize)}
}

// Publish enqueues an event; non-blocking (drops if buffer is full — acceptable for test harness).
func (b *Bus) Publish(_ context.Context, event PaymentEvent) {
	select {
	case b.ch <- event:
	default:
	}
}

// Subscribe returns the read-only event channel.
func (b *Bus) Subscribe() <-chan PaymentEvent {
	return b.ch
}
