using ApiGateway.Models;
using Grpc.Core;
using Microsoft.AspNetCore.Mvc;

namespace ApiGateway.Controllers;

/// <summary>Maps gRPC StatusCode to HTTP status + structured JSON error bodies.</summary>
internal static class GrpcErrorMapper
{
    public static IActionResult ToHttp(RpcException ex)
    {
        var code = ex.Message.Split(':')[0].Trim();
        var msg = ex.Status.Detail;

        return ex.StatusCode switch
        {
            StatusCode.NotFound         => new NotFoundObjectResult(new ErrorResponse(code, msg)),
            StatusCode.AlreadyExists    => new ConflictObjectResult(new ErrorResponse(code, msg)),
            StatusCode.PermissionDenied => new ObjectResult(new ErrorResponse(code, msg)) { StatusCode = 403 },
            StatusCode.InvalidArgument  => new UnprocessableEntityObjectResult(new ErrorResponse(code, msg)),
            StatusCode.FailedPrecondition => new UnprocessableEntityObjectResult(new ErrorResponse(code, msg)),
            _                           => new ObjectResult(new ErrorResponse("INTERNAL_ERROR", msg)) { StatusCode = 500 },
        };
    }
}
