using Pix.Payment.V1;
using Pix.Keys.V1;

var builder = WebApplication.CreateBuilder(args);

// gRPC clients — addresses resolved from environment variables (with defaults for local dev)
var paymentCoreAddr = builder.Configuration["PaymentCore:Address"] ?? "http://localhost:9090";
var keyMgmtAddr     = builder.Configuration["KeyManagement:Address"] ?? "http://localhost:9091";

builder.Services.AddGrpcClient<PaymentService.PaymentServiceClient>(o =>
    o.Address = new Uri(paymentCoreAddr));

builder.Services.AddGrpcClient<KeyManagementService.KeyManagementServiceClient>(o =>
    o.Address = new Uri(keyMgmtAddr));

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Add webhook registry (in-memory for the gateway tier)
builder.Services.AddSingleton<WebhookRegistry>();

var app = builder.Build();

app.UseRouting();
app.MapControllers();

app.Run();

// Expose type for integration tests
public partial class Program { }
