using ApiGateway.Models;
using Grpc.Core;
using Microsoft.AspNetCore.Mvc;
using Pix.Payment.V1;

namespace ApiGateway.Controllers;

/// <summary>
/// REST → gRPC translation for payment operations.
/// POST  /payments            — CreatePayment
/// POST  /payments/inbound    — ReceiveInboundCredit
/// GET   /payments/{id}       — GetPayment
/// GET   /payments            — ListPayments (state=, cursor=)
/// </summary>
[ApiController]
[Route("payments")]
public class PaymentsController : ControllerBase
{
    private readonly PaymentService.PaymentServiceClient _client;

    public PaymentsController(PaymentService.PaymentServiceClient client) => _client = client;

    // ── POST /payments ────────────────────────────────────────────────────────

    public record CreatePaymentRequest(
        string PayerKey,
        string PayeeKey,
        long AmountCentavos,
        string IdempotencyKey);

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] CreatePaymentRequest body)
    {
        try
        {
            var resp = await _client.CreatePaymentAsync(new CreatePaymentRequest
            {
                PayerKey       = body.PayerKey ?? "",
                PayeeKey       = body.PayeeKey ?? "",
                AmountCentavos = body.AmountCentavos,
                IdempotencyKey = body.IdempotencyKey ?? "",
            });
            return Ok(resp.Payment);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── POST /payments/inbound ────────────────────────────────────────────────

    public record InboundCreditRequest(
        string SpiEndToEndId,
        string PayerKey,
        string PayeeKey,
        long AmountCentavos);

    [HttpPost("inbound")]
    public async Task<IActionResult> ReceiveInbound([FromBody] InboundCreditRequest body)
    {
        try
        {
            var resp = await _client.ReceiveInboundCreditAsync(new ReceiveInboundCreditRequest
            {
                SpiEndToEndId  = body.SpiEndToEndId ?? "",
                PayerKey       = body.PayerKey      ?? "",
                PayeeKey       = body.PayeeKey      ?? "",
                AmountCentavos = body.AmountCentavos,
            });
            return Ok(resp.Payment);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── GET /payments/{id} ───────────────────────────────────────────────────

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(string id)
    {
        try
        {
            var resp = await _client.GetPaymentAsync(new GetPaymentRequest { Id = id });
            return Ok(resp.Payment);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── GET /payments ─────────────────────────────────────────────────────────

    [HttpGet]
    public async Task<IActionResult> List(
        [FromQuery] string? state,
        [FromQuery] string? cursor,
        [FromQuery] int pageSize = 20)
    {
        try
        {
            var req = new ListPaymentsRequest
            {
                Cursor   = cursor   ?? "",
                PageSize = pageSize,
            };
            if (!string.IsNullOrEmpty(state) &&
                Enum.TryParse<PaymentState>("PAYMENT_STATE_" + state.ToUpper(), out var parsedState))
            {
                req.StateFilter = parsedState;
            }

            var resp = await _client.ListPaymentsAsync(req);
            return Ok(new { payments = resp.Payments, nextCursor = resp.NextCursor });
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }
}
