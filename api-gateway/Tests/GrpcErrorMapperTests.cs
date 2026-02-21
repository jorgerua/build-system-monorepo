using ApiGateway.Controllers;
using ApiGateway.Models;
using Grpc.Core;
using Microsoft.AspNetCore.Mvc;
using Xunit;

namespace ApiGateway.Tests;

/// <summary>xUnit tests verifying gRPC error codes map to correct HTTP status codes.</summary>
public class GrpcErrorMapperTests
{
    private static RpcException Make(StatusCode code, string detail) =>
        new RpcException(new Status(code, detail));

    [Fact]
    public void NotFound_Maps404()
    {
        var result = GrpcErrorMapper.ToHttp(Make(StatusCode.NotFound, "PAYMENT_NOT_FOUND: x"));
        Assert.IsType<NotFoundObjectResult>(result);
    }

    [Fact]
    public void AlreadyExists_Maps409()
    {
        var result = GrpcErrorMapper.ToHttp(Make(StatusCode.AlreadyExists, "KEY_ALREADY_EXISTS: k"));
        Assert.IsType<ConflictObjectResult>(result);
    }

    [Fact]
    public void PermissionDenied_Maps403()
    {
        var result = GrpcErrorMapper.ToHttp(Make(StatusCode.PermissionDenied, "KEY_OWNERSHIP_MISMATCH")) as ObjectResult;
        Assert.NotNull(result);
        Assert.Equal(403, result!.StatusCode);
    }

    [Fact]
    public void InvalidArgument_Maps422()
    {
        var result = GrpcErrorMapper.ToHttp(Make(StatusCode.InvalidArgument, "INVALID_AMOUNT"));
        Assert.IsType<UnprocessableEntityObjectResult>(result);
    }

    [Fact]
    public void Internal_Maps500()
    {
        var result = GrpcErrorMapper.ToHttp(Make(StatusCode.Internal, "unexpected")) as ObjectResult;
        Assert.NotNull(result);
        Assert.Equal(500, result!.StatusCode);
        var err = result.Value as ErrorResponse;
        Assert.Equal("INTERNAL_ERROR", err?.Code);
    }
}
