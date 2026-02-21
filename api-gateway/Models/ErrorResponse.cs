namespace ApiGateway.Models;

/// <summary>Structured JSON error body returned on error responses.</summary>
public record ErrorResponse(string Code, string Message);
