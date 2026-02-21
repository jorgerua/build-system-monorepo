using ApiGateway.Controllers;
using ApiGateway.Models;
using Microsoft.AspNetCore.Mvc;
using Xunit;

namespace ApiGateway.Tests;

/// <summary>xUnit tests for WebhooksController — covers registration and deletion.</summary>
public class WebhooksControllerTests
{
    private static WebhooksController MakeController() =>
        new WebhooksController(new WebhookRegistry());

    [Fact]
    public void Register_ValidHttps_Returns200WithId()
    {
        var ctrl = MakeController();
        var result = ctrl.Register(new WebhooksController.RegisterWebhookRequest(
            "https://example.com/webhook", ["payment.settled"])) as OkObjectResult;

        Assert.NotNull(result);
        Assert.Equal(200, result!.StatusCode);
        dynamic body = result.Value!;
        Assert.NotNull(body);
    }

    [Fact]
    public void Register_HttpUrl_Returns422()
    {
        var ctrl = MakeController();
        var result = ctrl.Register(new WebhooksController.RegisterWebhookRequest(
            "http://example.com/webhook", [])) as UnprocessableEntityObjectResult;

        Assert.NotNull(result);
        var err = result!.Value as ErrorResponse;
        Assert.Equal("INVALID_WEBHOOK_URL", err?.Code);
    }

    [Fact]
    public void Register_NullUrl_Returns422()
    {
        var ctrl = MakeController();
        var result = ctrl.Register(new WebhooksController.RegisterWebhookRequest(null!, []));
        Assert.IsType<UnprocessableEntityObjectResult>(result);
    }

    [Fact]
    public void Delete_ExistingId_Returns204()
    {
        var registry = new WebhookRegistry();
        var entry = registry.Register("https://example.com/wh", ["payment.settled"]);
        var ctrl = new WebhooksController(registry);

        var result = ctrl.Delete(entry.Id);
        Assert.IsType<NoContentResult>(result);
    }

    [Fact]
    public void Delete_UnknownId_Returns404()
    {
        var ctrl = MakeController();
        var result = ctrl.Delete("no-such-id");
        Assert.IsType<NotFoundObjectResult>(result);
    }
}
