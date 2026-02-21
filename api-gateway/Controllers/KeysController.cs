using Grpc.Core;
using Microsoft.AspNetCore.Mvc;
using Pix.Keys.V1;

namespace ApiGateway.Controllers;

/// <summary>
/// REST → gRPC translation for PIX key management.
/// POST   /keys                              — RegisterKey
/// GET    /keys/{key}                        — LookupKey
/// DELETE /keys/{key}                        — DeleteKey
/// POST   /keys/{key}/portability            — InitiatePortability
/// POST   /keys/{key}/portability/confirm    — ConfirmPortability
/// </summary>
[ApiController]
[Route("keys")]
public class KeysController : ControllerBase
{
    private readonly KeyManagementService.KeyManagementServiceClient _client;

    public KeysController(KeyManagementService.KeyManagementServiceClient client) => _client = client;

    // ── POST /keys ────────────────────────────────────────────────────────────

    public record RegisterKeyRequest(
        string? Key,
        string KeyType,
        string OwnerId,
        string AccountHolder,
        string AccountBranch,
        string AccountNumber);

    [HttpPost]
    public async Task<IActionResult> Register([FromBody] RegisterKeyRequest body)
    {
        try
        {
            if (!Enum.TryParse<KeyType>("KEY_TYPE_" + (body.KeyType ?? "").ToUpper(), out var keyType))
                return UnprocessableEntity(new { code = "INVALID_KEY_TYPE", message = $"Unknown key type: {body.KeyType}" });

            var resp = await _client.RegisterKeyAsync(new Pix.Keys.V1.RegisterKeyRequest
            {
                Key           = body.Key           ?? "",
                KeyType       = keyType,
                OwnerId       = body.OwnerId       ?? "",
                AccountHolder = body.AccountHolder ?? "",
                AccountBranch = body.AccountBranch ?? "",
                AccountNumber = body.AccountNumber ?? "",
            });
            return Ok(resp.PixKey);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── GET /keys/{key} ───────────────────────────────────────────────────────

    [HttpGet("{key}")]
    public async Task<IActionResult> Lookup(string key)
    {
        try
        {
            var resp = await _client.LookupKeyAsync(new LookupKeyRequest { Key = key });
            return Ok(resp.PixKey);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── DELETE /keys/{key} ────────────────────────────────────────────────────

    [HttpDelete("{key}")]
    public async Task<IActionResult> Delete(string key, [FromQuery] string ownerId)
    {
        try
        {
            await _client.DeleteKeyAsync(new DeleteKeyRequest { Key = key, OwnerId = ownerId ?? "" });
            return NoContent();
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── POST /keys/{key}/portability ──────────────────────────────────────────

    public record InitiatePortabilityRequest(string RequestingOwner);

    [HttpPost("{key}/portability")]
    public async Task<IActionResult> InitiatePortability(string key, [FromBody] InitiatePortabilityRequest body)
    {
        try
        {
            var resp = await _client.InitiatePortabilityAsync(new Pix.Keys.V1.InitiatePortabilityRequest
            {
                Key              = key,
                RequestingOwner  = body.RequestingOwner ?? "",
            });
            return Ok(resp.Claim);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }

    // ── POST /keys/{key}/portability/confirm ──────────────────────────────────

    public record ConfirmPortabilityRequest(string ClaimId, string ConfirmingOwner);

    [HttpPost("{key}/portability/confirm")]
    public async Task<IActionResult> ConfirmPortability(string key, [FromBody] ConfirmPortabilityRequest body)
    {
        try
        {
            var resp = await _client.ConfirmPortabilityAsync(new Pix.Keys.V1.ConfirmPortabilityRequest
            {
                ClaimId         = body.ClaimId         ?? "",
                ConfirmingOwner = body.ConfirmingOwner ?? "",
            });
            return Ok(resp.PixKey);
        }
        catch (RpcException ex) { return GrpcErrorMapper.ToHttp(ex); }
    }
}
