package com.example.pix.keys.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * KeyManagementService exposes PIX key operations over gRPC.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.64.0)",
    comments = "Source: keys.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class KeyManagementServiceGrpc {

  private KeyManagementServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "pix.keys.v1.KeyManagementService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.pix.keys.v1.RegisterKeyRequest,
      com.example.pix.keys.v1.RegisterKeyResponse> getRegisterKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterKey",
      requestType = com.example.pix.keys.v1.RegisterKeyRequest.class,
      responseType = com.example.pix.keys.v1.RegisterKeyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.keys.v1.RegisterKeyRequest,
      com.example.pix.keys.v1.RegisterKeyResponse> getRegisterKeyMethod() {
    io.grpc.MethodDescriptor<com.example.pix.keys.v1.RegisterKeyRequest, com.example.pix.keys.v1.RegisterKeyResponse> getRegisterKeyMethod;
    if ((getRegisterKeyMethod = KeyManagementServiceGrpc.getRegisterKeyMethod) == null) {
      synchronized (KeyManagementServiceGrpc.class) {
        if ((getRegisterKeyMethod = KeyManagementServiceGrpc.getRegisterKeyMethod) == null) {
          KeyManagementServiceGrpc.getRegisterKeyMethod = getRegisterKeyMethod =
              io.grpc.MethodDescriptor.<com.example.pix.keys.v1.RegisterKeyRequest, com.example.pix.keys.v1.RegisterKeyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.RegisterKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.RegisterKeyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new KeyManagementServiceMethodDescriptorSupplier("RegisterKey"))
              .build();
        }
      }
    }
    return getRegisterKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.keys.v1.LookupKeyRequest,
      com.example.pix.keys.v1.LookupKeyResponse> getLookupKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LookupKey",
      requestType = com.example.pix.keys.v1.LookupKeyRequest.class,
      responseType = com.example.pix.keys.v1.LookupKeyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.keys.v1.LookupKeyRequest,
      com.example.pix.keys.v1.LookupKeyResponse> getLookupKeyMethod() {
    io.grpc.MethodDescriptor<com.example.pix.keys.v1.LookupKeyRequest, com.example.pix.keys.v1.LookupKeyResponse> getLookupKeyMethod;
    if ((getLookupKeyMethod = KeyManagementServiceGrpc.getLookupKeyMethod) == null) {
      synchronized (KeyManagementServiceGrpc.class) {
        if ((getLookupKeyMethod = KeyManagementServiceGrpc.getLookupKeyMethod) == null) {
          KeyManagementServiceGrpc.getLookupKeyMethod = getLookupKeyMethod =
              io.grpc.MethodDescriptor.<com.example.pix.keys.v1.LookupKeyRequest, com.example.pix.keys.v1.LookupKeyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LookupKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.LookupKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.LookupKeyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new KeyManagementServiceMethodDescriptorSupplier("LookupKey"))
              .build();
        }
      }
    }
    return getLookupKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.keys.v1.DeleteKeyRequest,
      com.example.pix.keys.v1.DeleteKeyResponse> getDeleteKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteKey",
      requestType = com.example.pix.keys.v1.DeleteKeyRequest.class,
      responseType = com.example.pix.keys.v1.DeleteKeyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.keys.v1.DeleteKeyRequest,
      com.example.pix.keys.v1.DeleteKeyResponse> getDeleteKeyMethod() {
    io.grpc.MethodDescriptor<com.example.pix.keys.v1.DeleteKeyRequest, com.example.pix.keys.v1.DeleteKeyResponse> getDeleteKeyMethod;
    if ((getDeleteKeyMethod = KeyManagementServiceGrpc.getDeleteKeyMethod) == null) {
      synchronized (KeyManagementServiceGrpc.class) {
        if ((getDeleteKeyMethod = KeyManagementServiceGrpc.getDeleteKeyMethod) == null) {
          KeyManagementServiceGrpc.getDeleteKeyMethod = getDeleteKeyMethod =
              io.grpc.MethodDescriptor.<com.example.pix.keys.v1.DeleteKeyRequest, com.example.pix.keys.v1.DeleteKeyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteKey"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.DeleteKeyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.DeleteKeyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new KeyManagementServiceMethodDescriptorSupplier("DeleteKey"))
              .build();
        }
      }
    }
    return getDeleteKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.keys.v1.InitiatePortabilityRequest,
      com.example.pix.keys.v1.InitiatePortabilityResponse> getInitiatePortabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitiatePortability",
      requestType = com.example.pix.keys.v1.InitiatePortabilityRequest.class,
      responseType = com.example.pix.keys.v1.InitiatePortabilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.keys.v1.InitiatePortabilityRequest,
      com.example.pix.keys.v1.InitiatePortabilityResponse> getInitiatePortabilityMethod() {
    io.grpc.MethodDescriptor<com.example.pix.keys.v1.InitiatePortabilityRequest, com.example.pix.keys.v1.InitiatePortabilityResponse> getInitiatePortabilityMethod;
    if ((getInitiatePortabilityMethod = KeyManagementServiceGrpc.getInitiatePortabilityMethod) == null) {
      synchronized (KeyManagementServiceGrpc.class) {
        if ((getInitiatePortabilityMethod = KeyManagementServiceGrpc.getInitiatePortabilityMethod) == null) {
          KeyManagementServiceGrpc.getInitiatePortabilityMethod = getInitiatePortabilityMethod =
              io.grpc.MethodDescriptor.<com.example.pix.keys.v1.InitiatePortabilityRequest, com.example.pix.keys.v1.InitiatePortabilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InitiatePortability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.InitiatePortabilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.InitiatePortabilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new KeyManagementServiceMethodDescriptorSupplier("InitiatePortability"))
              .build();
        }
      }
    }
    return getInitiatePortabilityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.keys.v1.ConfirmPortabilityRequest,
      com.example.pix.keys.v1.ConfirmPortabilityResponse> getConfirmPortabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ConfirmPortability",
      requestType = com.example.pix.keys.v1.ConfirmPortabilityRequest.class,
      responseType = com.example.pix.keys.v1.ConfirmPortabilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.keys.v1.ConfirmPortabilityRequest,
      com.example.pix.keys.v1.ConfirmPortabilityResponse> getConfirmPortabilityMethod() {
    io.grpc.MethodDescriptor<com.example.pix.keys.v1.ConfirmPortabilityRequest, com.example.pix.keys.v1.ConfirmPortabilityResponse> getConfirmPortabilityMethod;
    if ((getConfirmPortabilityMethod = KeyManagementServiceGrpc.getConfirmPortabilityMethod) == null) {
      synchronized (KeyManagementServiceGrpc.class) {
        if ((getConfirmPortabilityMethod = KeyManagementServiceGrpc.getConfirmPortabilityMethod) == null) {
          KeyManagementServiceGrpc.getConfirmPortabilityMethod = getConfirmPortabilityMethod =
              io.grpc.MethodDescriptor.<com.example.pix.keys.v1.ConfirmPortabilityRequest, com.example.pix.keys.v1.ConfirmPortabilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ConfirmPortability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.ConfirmPortabilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.keys.v1.ConfirmPortabilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new KeyManagementServiceMethodDescriptorSupplier("ConfirmPortability"))
              .build();
        }
      }
    }
    return getConfirmPortabilityMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static KeyManagementServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<KeyManagementServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<KeyManagementServiceStub>() {
        @java.lang.Override
        public KeyManagementServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new KeyManagementServiceStub(channel, callOptions);
        }
      };
    return KeyManagementServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static KeyManagementServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<KeyManagementServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<KeyManagementServiceBlockingStub>() {
        @java.lang.Override
        public KeyManagementServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new KeyManagementServiceBlockingStub(channel, callOptions);
        }
      };
    return KeyManagementServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static KeyManagementServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<KeyManagementServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<KeyManagementServiceFutureStub>() {
        @java.lang.Override
        public KeyManagementServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new KeyManagementServiceFutureStub(channel, callOptions);
        }
      };
    return KeyManagementServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * KeyManagementService exposes PIX key operations over gRPC.
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void registerKey(com.example.pix.keys.v1.RegisterKeyRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.RegisterKeyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterKeyMethod(), responseObserver);
    }

    /**
     */
    default void lookupKey(com.example.pix.keys.v1.LookupKeyRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.LookupKeyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLookupKeyMethod(), responseObserver);
    }

    /**
     */
    default void deleteKey(com.example.pix.keys.v1.DeleteKeyRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.DeleteKeyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteKeyMethod(), responseObserver);
    }

    /**
     */
    default void initiatePortability(com.example.pix.keys.v1.InitiatePortabilityRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.InitiatePortabilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInitiatePortabilityMethod(), responseObserver);
    }

    /**
     */
    default void confirmPortability(com.example.pix.keys.v1.ConfirmPortabilityRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.ConfirmPortabilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getConfirmPortabilityMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service KeyManagementService.
   * <pre>
   * KeyManagementService exposes PIX key operations over gRPC.
   * </pre>
   */
  public static abstract class KeyManagementServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return KeyManagementServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service KeyManagementService.
   * <pre>
   * KeyManagementService exposes PIX key operations over gRPC.
   * </pre>
   */
  public static final class KeyManagementServiceStub
      extends io.grpc.stub.AbstractAsyncStub<KeyManagementServiceStub> {
    private KeyManagementServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KeyManagementServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new KeyManagementServiceStub(channel, callOptions);
    }

    /**
     */
    public void registerKey(com.example.pix.keys.v1.RegisterKeyRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.RegisterKeyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lookupKey(com.example.pix.keys.v1.LookupKeyRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.LookupKeyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLookupKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteKey(com.example.pix.keys.v1.DeleteKeyRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.DeleteKeyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteKeyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initiatePortability(com.example.pix.keys.v1.InitiatePortabilityRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.InitiatePortabilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInitiatePortabilityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void confirmPortability(com.example.pix.keys.v1.ConfirmPortabilityRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.keys.v1.ConfirmPortabilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getConfirmPortabilityMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service KeyManagementService.
   * <pre>
   * KeyManagementService exposes PIX key operations over gRPC.
   * </pre>
   */
  public static final class KeyManagementServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<KeyManagementServiceBlockingStub> {
    private KeyManagementServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KeyManagementServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new KeyManagementServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.pix.keys.v1.RegisterKeyResponse registerKey(com.example.pix.keys.v1.RegisterKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterKeyMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.keys.v1.LookupKeyResponse lookupKey(com.example.pix.keys.v1.LookupKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLookupKeyMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.keys.v1.DeleteKeyResponse deleteKey(com.example.pix.keys.v1.DeleteKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteKeyMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.keys.v1.InitiatePortabilityResponse initiatePortability(com.example.pix.keys.v1.InitiatePortabilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInitiatePortabilityMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.keys.v1.ConfirmPortabilityResponse confirmPortability(com.example.pix.keys.v1.ConfirmPortabilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getConfirmPortabilityMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service KeyManagementService.
   * <pre>
   * KeyManagementService exposes PIX key operations over gRPC.
   * </pre>
   */
  public static final class KeyManagementServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<KeyManagementServiceFutureStub> {
    private KeyManagementServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KeyManagementServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new KeyManagementServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.keys.v1.RegisterKeyResponse> registerKey(
        com.example.pix.keys.v1.RegisterKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.keys.v1.LookupKeyResponse> lookupKey(
        com.example.pix.keys.v1.LookupKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLookupKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.keys.v1.DeleteKeyResponse> deleteKey(
        com.example.pix.keys.v1.DeleteKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.keys.v1.InitiatePortabilityResponse> initiatePortability(
        com.example.pix.keys.v1.InitiatePortabilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInitiatePortabilityMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.keys.v1.ConfirmPortabilityResponse> confirmPortability(
        com.example.pix.keys.v1.ConfirmPortabilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getConfirmPortabilityMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_KEY = 0;
  private static final int METHODID_LOOKUP_KEY = 1;
  private static final int METHODID_DELETE_KEY = 2;
  private static final int METHODID_INITIATE_PORTABILITY = 3;
  private static final int METHODID_CONFIRM_PORTABILITY = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_KEY:
          serviceImpl.registerKey((com.example.pix.keys.v1.RegisterKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.keys.v1.RegisterKeyResponse>) responseObserver);
          break;
        case METHODID_LOOKUP_KEY:
          serviceImpl.lookupKey((com.example.pix.keys.v1.LookupKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.keys.v1.LookupKeyResponse>) responseObserver);
          break;
        case METHODID_DELETE_KEY:
          serviceImpl.deleteKey((com.example.pix.keys.v1.DeleteKeyRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.keys.v1.DeleteKeyResponse>) responseObserver);
          break;
        case METHODID_INITIATE_PORTABILITY:
          serviceImpl.initiatePortability((com.example.pix.keys.v1.InitiatePortabilityRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.keys.v1.InitiatePortabilityResponse>) responseObserver);
          break;
        case METHODID_CONFIRM_PORTABILITY:
          serviceImpl.confirmPortability((com.example.pix.keys.v1.ConfirmPortabilityRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.keys.v1.ConfirmPortabilityResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRegisterKeyMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.keys.v1.RegisterKeyRequest,
              com.example.pix.keys.v1.RegisterKeyResponse>(
                service, METHODID_REGISTER_KEY)))
        .addMethod(
          getLookupKeyMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.keys.v1.LookupKeyRequest,
              com.example.pix.keys.v1.LookupKeyResponse>(
                service, METHODID_LOOKUP_KEY)))
        .addMethod(
          getDeleteKeyMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.keys.v1.DeleteKeyRequest,
              com.example.pix.keys.v1.DeleteKeyResponse>(
                service, METHODID_DELETE_KEY)))
        .addMethod(
          getInitiatePortabilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.keys.v1.InitiatePortabilityRequest,
              com.example.pix.keys.v1.InitiatePortabilityResponse>(
                service, METHODID_INITIATE_PORTABILITY)))
        .addMethod(
          getConfirmPortabilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.keys.v1.ConfirmPortabilityRequest,
              com.example.pix.keys.v1.ConfirmPortabilityResponse>(
                service, METHODID_CONFIRM_PORTABILITY)))
        .build();
  }

  private static abstract class KeyManagementServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    KeyManagementServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.pix.keys.v1.Keys.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("KeyManagementService");
    }
  }

  private static final class KeyManagementServiceFileDescriptorSupplier
      extends KeyManagementServiceBaseDescriptorSupplier {
    KeyManagementServiceFileDescriptorSupplier() {}
  }

  private static final class KeyManagementServiceMethodDescriptorSupplier
      extends KeyManagementServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    KeyManagementServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (KeyManagementServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new KeyManagementServiceFileDescriptorSupplier())
              .addMethod(getRegisterKeyMethod())
              .addMethod(getLookupKeyMethod())
              .addMethod(getDeleteKeyMethod())
              .addMethod(getInitiatePortabilityMethod())
              .addMethod(getConfirmPortabilityMethod())
              .build();
        }
      }
    }
    return result;
  }
}
