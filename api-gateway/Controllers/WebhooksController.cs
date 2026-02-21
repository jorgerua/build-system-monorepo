using ApiGateway.Models;
using Microsoft.AspNetCore.Mvc;

namespace ApiGateway.Controllers;

/// <summary>
/// REST endpoints for webhook management.
/// POST   /webhooks        — Register a webhook
/// DELETE /webhooks/{id}   — Delete a webhook
/// </summary>
[ApiController]
[Route("webhooks")]
public class WebhooksController : ControllerBase
{
    private readonly WebhookRegistry _registry;

    public WebhooksController(WebhookRegistry registry) => _registry = registry;

    public record RegisterWebhookRequest(string Url, IReadOnlyList<string> EventTypes);

    [HttpPost]
    public IActionResult Register([FromBody] RegisterWebhookRequest body)
    {
        if (string.IsNullOrEmpty(body?.Url))
            return UnprocessableEntity(new ErrorResponse("MISSING_FIELD", "url is required"));

        if (!body.Url.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
            return UnprocessableEntity(new ErrorResponse("INVALID_WEBHOOK_URL", "only HTTPS URLs are accepted"));

        var entry = _registry.Register(body.Url, body.EventTypes ?? []);
        return Ok(new { id = entry.Id, url = entry.Url, eventTypes = entry.EventTypes });
    }

    [HttpDelete("{id}")]
    public IActionResult Delete(string id)
    {
        if (_registry.Delete(id))
            return NoContent();
        return NotFound(new ErrorResponse("WEBHOOK_NOT_FOUND", $"No webhook with id {id}"));
    }
}
