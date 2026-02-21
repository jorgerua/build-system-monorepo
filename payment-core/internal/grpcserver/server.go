// Package grpcserver wires the gRPC PaymentService implementation.
package grpcserver

import (
	"context"
	"fmt"
	"strconv"
	"time"

	"github.com/example/pix/payment-core/internal/domain"
	"github.com/example/pix/payment-core/internal/eventbus"
	"github.com/example/pix/payment-core/internal/spi"
	"github.com/example/pix/payment-core/internal/storage"
	"github.com/google/uuid"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"

	paymentv1 "github.com/example/pix/payment-core/gen/payment/v1"
)

// Server implements paymentv1.PaymentServiceServer.
type Server struct {
	paymentv1.UnimplementedPaymentServiceServer
	db        *storage.DB
	spiClient spi.Client
	bus       eventbus.Publisher
}

// New returns a new Server.
func New(db *storage.DB, spiClient spi.Client, bus eventbus.Publisher) *Server {
	return &Server{db: db, spiClient: spiClient, bus: bus}
}

// CreatePayment implements the CreatePayment RPC.
// Deduplicates on idempotency key; validates amount limits and required fields.
func (s *Server) CreatePayment(ctx context.Context, req *paymentv1.CreatePaymentRequest) (*paymentv1.CreatePaymentResponse, error) {
	// Idempotency check
	if req.IdempotencyKey != "" {
		existing, err := s.db.GetPaymentByIdempotencyKey(ctx, req.IdempotencyKey)
		if err != nil {
			return nil, status.Errorf(codes.Internal, "idempotency check: %v", err)
		}
		if existing != nil {
			return &paymentv1.CreatePaymentResponse{Payment: toProto(existing)}, nil
		}
	}

	p, err := domain.NewPayment(req.PayerKey, req.PayeeKey, req.AmountCentavos, req.IdempotencyKey)
	if err != nil {
		return nil, status.Errorf(codes.InvalidArgument, "%v", err)
	}

	if err := s.db.CreatePayment(ctx, p); err != nil {
		return nil, status.Errorf(codes.Internal, "persist payment: %v", err)
	}

	s.publishEvent(ctx, p, "", domain.StateCreated, "")
	return &paymentv1.CreatePaymentResponse{Payment: toProto(p)}, nil
}

// GetPayment implements the GetPayment RPC.
func (s *Server) GetPayment(ctx context.Context, req *paymentv1.GetPaymentRequest) (*paymentv1.GetPaymentResponse, error) {
	p, err := s.db.GetPaymentByID(ctx, req.Id)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "get payment: %v", err)
	}
	if p == nil {
		return nil, status.Errorf(codes.NotFound, "PAYMENT_NOT_FOUND: %s", req.Id)
	}
	return &paymentv1.GetPaymentResponse{Payment: toProto(p)}, nil
}

// ListPayments implements the ListPayments RPC with cursor-based pagination.
func (s *Server) ListPayments(ctx context.Context, req *paymentv1.ListPaymentsRequest) (*paymentv1.ListPaymentsResponse, error) {
	var stateFilter domain.PaymentState
	if req.StateFilter != paymentv1.PaymentState_PAYMENT_STATE_UNSPECIFIED {
		stateFilter = protoStateToModel(req.StateFilter)
	}

	var cursor int64
	if req.Cursor != "" {
		var err error
		cursor, err = strconv.ParseInt(req.Cursor, 10, 64)
		if err != nil {
			return nil, status.Errorf(codes.InvalidArgument, "invalid cursor: %v", err)
		}
	}

	pageSize := int(req.PageSize)
	if pageSize <= 0 {
		pageSize = 20
	}
	if pageSize > 100 {
		pageSize = 100
	}

	payments, err := s.db.ListPaymentsPage(ctx, stateFilter, cursor, pageSize)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "list payments: %v", err)
	}

	resp := &paymentv1.ListPaymentsResponse{}
	for _, p := range payments {
		resp.Payments = append(resp.Payments, toProto(p))
	}

	// Set next cursor to the created_at of the last item (Unix timestamp as string).
	if len(payments) == pageSize {
		resp.NextCursor = strconv.FormatInt(payments[len(payments)-1].CreatedAt.Unix(), 10)
	}

	return resp, nil
}

// SubmitToSPI implements the SubmitToSPI RPC.
func (s *Server) SubmitToSPI(ctx context.Context, req *paymentv1.SubmitToSPIRequest) (*paymentv1.SubmitToSPIResponse, error) {
	p, err := s.db.GetPaymentByID(ctx, req.PaymentId)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "fetch payment: %v", err)
	}
	if p == nil {
		return nil, status.Errorf(codes.NotFound, "PAYMENT_NOT_FOUND: %s", req.PaymentId)
	}
	if p.State != domain.StateCreated {
		return nil, status.Errorf(codes.FailedPrecondition, "INVALID_STATE_TRANSITION: payment is in %s", p.State)
	}

	// CREATED → VALIDATING
	prevState := p.State
	if err := p.Transition(domain.StateValidating); err != nil {
		return nil, status.Errorf(codes.Internal, "%v", err)
	}
	if err := s.db.UpdatePaymentState(ctx, p, prevState, ""); err != nil {
		return nil, status.Errorf(codes.Internal, "update state: %v", err)
	}
	s.publishEvent(ctx, p, string(prevState), domain.StateValidating, "")

	// Call SPI stub
	result, err := s.spiClient.SubmitPayment(ctx, p.ID, p.PayerKey, p.PayeeKey, p.AmountCentavos)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "spi submit: %v", err)
	}

	// VALIDATING → SUBMITTED or FAILED
	prevState = p.State
	if result.Accepted {
		p.SPIEndToEndID = result.EndToEndID
		_ = p.Transition(domain.StateSubmitted)
		_ = s.db.UpdatePaymentState(ctx, p, prevState, "")
		s.publishEvent(ctx, p, string(prevState), domain.StateSubmitted, "")
	} else {
		p.RejectionReason = result.RejectionCode
		_ = p.Transition(domain.StateFailed)
		_ = s.db.UpdatePaymentState(ctx, p, prevState, result.RejectionCode)
		s.publishEvent(ctx, p, string(prevState), domain.StateFailed, result.RejectionCode)
	}

	return &paymentv1.SubmitToSPIResponse{Payment: toProto(p)}, nil
}

// ReceiveInboundCredit implements the inbound credit handler.
// Idempotent on SPI end-to-end ID; enforces 10-second processing timeout;
// checks payee key registration (stub: always accepts for now, key-management owns this).
func (s *Server) ReceiveInboundCredit(ctx context.Context, req *paymentv1.ReceiveInboundCreditRequest) (*paymentv1.ReceiveInboundCreditResponse, error) {
	if req.SpiEndToEndId == "" || req.PayerKey == "" || req.PayeeKey == "" || req.AmountCentavos <= 0 {
		return nil, status.Errorf(codes.InvalidArgument, "MISSING_FIELD: spi_end_to_end_id, payer_key, payee_key, amount_centavos are required")
	}

	// Enforce 10-second processing timeout
	ctx, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	// Idempotency check on SPI end-to-end ID
	existing, err := s.db.GetPaymentBySPIEndToEndID(ctx, req.SpiEndToEndId)
	if err != nil {
		return nil, status.Errorf(codes.Internal, "idempotency check: %v", err)
	}
	if existing != nil {
		return &paymentv1.ReceiveInboundCreditResponse{Payment: toProto(existing)}, nil
	}

	// Create inbound payment directly in SETTLED state
	now := time.Now().UTC()
	p := &domain.Payment{
		ID:            uuid.New().String(),
		State:         domain.StateSettled,
		PayerKey:      req.PayerKey,
		PayeeKey:      req.PayeeKey,
		AmountCentavos: req.AmountCentavos,
		IdempotencyKey: fmt.Sprintf("inbound:%s", req.SpiEndToEndId),
		SPIEndToEndID:  req.SpiEndToEndId,
		CreatedAt:      now,
		UpdatedAt:      now,
	}

	if err := s.db.CreatePayment(ctx, p); err != nil {
		if ctx.Err() != nil {
			return nil, status.Errorf(codes.DeadlineExceeded, "processing timeout exceeded")
		}
		return nil, status.Errorf(codes.Internal, "persist inbound payment: %v", err)
	}

	s.publishEvent(ctx, p, "", domain.StateSettled, "")
	return &paymentv1.ReceiveInboundCreditResponse{Payment: toProto(p)}, nil
}

func (s *Server) publishEvent(ctx context.Context, p *domain.Payment, prevState string, newState domain.PaymentState, reasonCode string) {
	s.bus.Publish(ctx, eventbus.PaymentEvent{
		EventID:       uuid.New().String(),
		PaymentID:     p.ID,
		EventType:     "payment." + stateToEventType(newState),
		PreviousState: prevState,
		NewState:      string(newState),
		OccurredAt:    time.Now().Unix(),
		ReasonCode:    reasonCode,
	})
}

func stateToEventType(s domain.PaymentState) string {
	switch s {
	case domain.StateCreated:
		return "created"
	case domain.StateValidating:
		return "validating"
	case domain.StateSubmitted:
		return "submitted"
	case domain.StateSettled:
		return "settled"
	case domain.StateFailed:
		return "failed"
	case domain.StateReversing:
		return "reversing"
	case domain.StateReversed:
		return "reversed"
	default:
		return "unknown"
	}
}

func toProto(p *domain.Payment) *paymentv1.Payment {
	return &paymentv1.Payment{
		Id:              p.ID,
		State:           modelStateToProto(p.State),
		PayerKey:        p.PayerKey,
		PayeeKey:        p.PayeeKey,
		AmountCentavos:  p.AmountCentavos,
		IdempotencyKey:  p.IdempotencyKey,
		SpiEndToEndId:   p.SPIEndToEndID,
		RejectionReason: p.RejectionReason,
		CreatedAtUnix:   p.CreatedAt.Unix(),
		UpdatedAtUnix:   p.UpdatedAt.Unix(),
	}
}

func modelStateToProto(s domain.PaymentState) paymentv1.PaymentState {
	switch s {
	case domain.StateCreated:
		return paymentv1.PaymentState_PAYMENT_STATE_CREATED
	case domain.StateValidating:
		return paymentv1.PaymentState_PAYMENT_STATE_VALIDATING
	case domain.StateSubmitted:
		return paymentv1.PaymentState_PAYMENT_STATE_SUBMITTED
	case domain.StateSettled:
		return paymentv1.PaymentState_PAYMENT_STATE_SETTLED
	case domain.StateFailed:
		return paymentv1.PaymentState_PAYMENT_STATE_FAILED
	case domain.StateReversing:
		return paymentv1.PaymentState_PAYMENT_STATE_REVERSING
	case domain.StateReversed:
		return paymentv1.PaymentState_PAYMENT_STATE_REVERSED
	default:
		return paymentv1.PaymentState_PAYMENT_STATE_UNSPECIFIED
	}
}

func protoStateToModel(s paymentv1.PaymentState) domain.PaymentState {
	switch s {
	case paymentv1.PaymentState_PAYMENT_STATE_CREATED:
		return domain.StateCreated
	case paymentv1.PaymentState_PAYMENT_STATE_VALIDATING:
		return domain.StateValidating
	case paymentv1.PaymentState_PAYMENT_STATE_SUBMITTED:
		return domain.StateSubmitted
	case paymentv1.PaymentState_PAYMENT_STATE_SETTLED:
		return domain.StateSettled
	case paymentv1.PaymentState_PAYMENT_STATE_FAILED:
		return domain.StateFailed
	case paymentv1.PaymentState_PAYMENT_STATE_REVERSING:
		return domain.StateReversing
	case paymentv1.PaymentState_PAYMENT_STATE_REVERSED:
		return domain.StateReversed
	default:
		return ""
	}
}
