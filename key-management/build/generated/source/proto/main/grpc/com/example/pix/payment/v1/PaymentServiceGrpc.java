package com.example.pix.payment.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * PaymentService exposes payment operations over gRPC.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.64.0)",
    comments = "Source: payment.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PaymentServiceGrpc {

  private PaymentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "pix.payment.v1.PaymentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.pix.payment.v1.CreatePaymentRequest,
      com.example.pix.payment.v1.CreatePaymentResponse> getCreatePaymentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreatePayment",
      requestType = com.example.pix.payment.v1.CreatePaymentRequest.class,
      responseType = com.example.pix.payment.v1.CreatePaymentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.payment.v1.CreatePaymentRequest,
      com.example.pix.payment.v1.CreatePaymentResponse> getCreatePaymentMethod() {
    io.grpc.MethodDescriptor<com.example.pix.payment.v1.CreatePaymentRequest, com.example.pix.payment.v1.CreatePaymentResponse> getCreatePaymentMethod;
    if ((getCreatePaymentMethod = PaymentServiceGrpc.getCreatePaymentMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getCreatePaymentMethod = PaymentServiceGrpc.getCreatePaymentMethod) == null) {
          PaymentServiceGrpc.getCreatePaymentMethod = getCreatePaymentMethod =
              io.grpc.MethodDescriptor.<com.example.pix.payment.v1.CreatePaymentRequest, com.example.pix.payment.v1.CreatePaymentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreatePayment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.CreatePaymentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.CreatePaymentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("CreatePayment"))
              .build();
        }
      }
    }
    return getCreatePaymentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.payment.v1.GetPaymentRequest,
      com.example.pix.payment.v1.GetPaymentResponse> getGetPaymentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetPayment",
      requestType = com.example.pix.payment.v1.GetPaymentRequest.class,
      responseType = com.example.pix.payment.v1.GetPaymentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.payment.v1.GetPaymentRequest,
      com.example.pix.payment.v1.GetPaymentResponse> getGetPaymentMethod() {
    io.grpc.MethodDescriptor<com.example.pix.payment.v1.GetPaymentRequest, com.example.pix.payment.v1.GetPaymentResponse> getGetPaymentMethod;
    if ((getGetPaymentMethod = PaymentServiceGrpc.getGetPaymentMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getGetPaymentMethod = PaymentServiceGrpc.getGetPaymentMethod) == null) {
          PaymentServiceGrpc.getGetPaymentMethod = getGetPaymentMethod =
              io.grpc.MethodDescriptor.<com.example.pix.payment.v1.GetPaymentRequest, com.example.pix.payment.v1.GetPaymentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetPayment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.GetPaymentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.GetPaymentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("GetPayment"))
              .build();
        }
      }
    }
    return getGetPaymentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.payment.v1.ListPaymentsRequest,
      com.example.pix.payment.v1.ListPaymentsResponse> getListPaymentsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListPayments",
      requestType = com.example.pix.payment.v1.ListPaymentsRequest.class,
      responseType = com.example.pix.payment.v1.ListPaymentsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.payment.v1.ListPaymentsRequest,
      com.example.pix.payment.v1.ListPaymentsResponse> getListPaymentsMethod() {
    io.grpc.MethodDescriptor<com.example.pix.payment.v1.ListPaymentsRequest, com.example.pix.payment.v1.ListPaymentsResponse> getListPaymentsMethod;
    if ((getListPaymentsMethod = PaymentServiceGrpc.getListPaymentsMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getListPaymentsMethod = PaymentServiceGrpc.getListPaymentsMethod) == null) {
          PaymentServiceGrpc.getListPaymentsMethod = getListPaymentsMethod =
              io.grpc.MethodDescriptor.<com.example.pix.payment.v1.ListPaymentsRequest, com.example.pix.payment.v1.ListPaymentsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListPayments"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.ListPaymentsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.ListPaymentsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("ListPayments"))
              .build();
        }
      }
    }
    return getListPaymentsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.payment.v1.SubmitToSPIRequest,
      com.example.pix.payment.v1.SubmitToSPIResponse> getSubmitToSPIMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitToSPI",
      requestType = com.example.pix.payment.v1.SubmitToSPIRequest.class,
      responseType = com.example.pix.payment.v1.SubmitToSPIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.payment.v1.SubmitToSPIRequest,
      com.example.pix.payment.v1.SubmitToSPIResponse> getSubmitToSPIMethod() {
    io.grpc.MethodDescriptor<com.example.pix.payment.v1.SubmitToSPIRequest, com.example.pix.payment.v1.SubmitToSPIResponse> getSubmitToSPIMethod;
    if ((getSubmitToSPIMethod = PaymentServiceGrpc.getSubmitToSPIMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getSubmitToSPIMethod = PaymentServiceGrpc.getSubmitToSPIMethod) == null) {
          PaymentServiceGrpc.getSubmitToSPIMethod = getSubmitToSPIMethod =
              io.grpc.MethodDescriptor.<com.example.pix.payment.v1.SubmitToSPIRequest, com.example.pix.payment.v1.SubmitToSPIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitToSPI"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.SubmitToSPIRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.SubmitToSPIResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("SubmitToSPI"))
              .build();
        }
      }
    }
    return getSubmitToSPIMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.pix.payment.v1.ReceiveInboundCreditRequest,
      com.example.pix.payment.v1.ReceiveInboundCreditResponse> getReceiveInboundCreditMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReceiveInboundCredit",
      requestType = com.example.pix.payment.v1.ReceiveInboundCreditRequest.class,
      responseType = com.example.pix.payment.v1.ReceiveInboundCreditResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.pix.payment.v1.ReceiveInboundCreditRequest,
      com.example.pix.payment.v1.ReceiveInboundCreditResponse> getReceiveInboundCreditMethod() {
    io.grpc.MethodDescriptor<com.example.pix.payment.v1.ReceiveInboundCreditRequest, com.example.pix.payment.v1.ReceiveInboundCreditResponse> getReceiveInboundCreditMethod;
    if ((getReceiveInboundCreditMethod = PaymentServiceGrpc.getReceiveInboundCreditMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getReceiveInboundCreditMethod = PaymentServiceGrpc.getReceiveInboundCreditMethod) == null) {
          PaymentServiceGrpc.getReceiveInboundCreditMethod = getReceiveInboundCreditMethod =
              io.grpc.MethodDescriptor.<com.example.pix.payment.v1.ReceiveInboundCreditRequest, com.example.pix.payment.v1.ReceiveInboundCreditResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReceiveInboundCredit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.ReceiveInboundCreditRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.pix.payment.v1.ReceiveInboundCreditResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("ReceiveInboundCredit"))
              .build();
        }
      }
    }
    return getReceiveInboundCreditMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PaymentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PaymentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PaymentServiceStub>() {
        @java.lang.Override
        public PaymentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PaymentServiceStub(channel, callOptions);
        }
      };
    return PaymentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PaymentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PaymentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PaymentServiceBlockingStub>() {
        @java.lang.Override
        public PaymentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PaymentServiceBlockingStub(channel, callOptions);
        }
      };
    return PaymentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PaymentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PaymentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PaymentServiceFutureStub>() {
        @java.lang.Override
        public PaymentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PaymentServiceFutureStub(channel, callOptions);
        }
      };
    return PaymentServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * PaymentService exposes payment operations over gRPC.
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void createPayment(com.example.pix.payment.v1.CreatePaymentRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.CreatePaymentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreatePaymentMethod(), responseObserver);
    }

    /**
     */
    default void getPayment(com.example.pix.payment.v1.GetPaymentRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.GetPaymentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetPaymentMethod(), responseObserver);
    }

    /**
     */
    default void listPayments(com.example.pix.payment.v1.ListPaymentsRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.ListPaymentsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListPaymentsMethod(), responseObserver);
    }

    /**
     */
    default void submitToSPI(com.example.pix.payment.v1.SubmitToSPIRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.SubmitToSPIResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitToSPIMethod(), responseObserver);
    }

    /**
     */
    default void receiveInboundCredit(com.example.pix.payment.v1.ReceiveInboundCreditRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.ReceiveInboundCreditResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReceiveInboundCreditMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PaymentService.
   * <pre>
   * PaymentService exposes payment operations over gRPC.
   * </pre>
   */
  public static abstract class PaymentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PaymentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PaymentService.
   * <pre>
   * PaymentService exposes payment operations over gRPC.
   * </pre>
   */
  public static final class PaymentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PaymentServiceStub> {
    private PaymentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PaymentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PaymentServiceStub(channel, callOptions);
    }

    /**
     */
    public void createPayment(com.example.pix.payment.v1.CreatePaymentRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.CreatePaymentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreatePaymentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getPayment(com.example.pix.payment.v1.GetPaymentRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.GetPaymentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetPaymentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listPayments(com.example.pix.payment.v1.ListPaymentsRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.ListPaymentsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListPaymentsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void submitToSPI(com.example.pix.payment.v1.SubmitToSPIRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.SubmitToSPIResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitToSPIMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void receiveInboundCredit(com.example.pix.payment.v1.ReceiveInboundCreditRequest request,
        io.grpc.stub.StreamObserver<com.example.pix.payment.v1.ReceiveInboundCreditResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReceiveInboundCreditMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PaymentService.
   * <pre>
   * PaymentService exposes payment operations over gRPC.
   * </pre>
   */
  public static final class PaymentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PaymentServiceBlockingStub> {
    private PaymentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PaymentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PaymentServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.pix.payment.v1.CreatePaymentResponse createPayment(com.example.pix.payment.v1.CreatePaymentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreatePaymentMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.payment.v1.GetPaymentResponse getPayment(com.example.pix.payment.v1.GetPaymentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetPaymentMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.payment.v1.ListPaymentsResponse listPayments(com.example.pix.payment.v1.ListPaymentsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListPaymentsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.payment.v1.SubmitToSPIResponse submitToSPI(com.example.pix.payment.v1.SubmitToSPIRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitToSPIMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.example.pix.payment.v1.ReceiveInboundCreditResponse receiveInboundCredit(com.example.pix.payment.v1.ReceiveInboundCreditRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReceiveInboundCreditMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PaymentService.
   * <pre>
   * PaymentService exposes payment operations over gRPC.
   * </pre>
   */
  public static final class PaymentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PaymentServiceFutureStub> {
    private PaymentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PaymentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PaymentServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.payment.v1.CreatePaymentResponse> createPayment(
        com.example.pix.payment.v1.CreatePaymentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreatePaymentMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.payment.v1.GetPaymentResponse> getPayment(
        com.example.pix.payment.v1.GetPaymentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetPaymentMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.payment.v1.ListPaymentsResponse> listPayments(
        com.example.pix.payment.v1.ListPaymentsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListPaymentsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.payment.v1.SubmitToSPIResponse> submitToSPI(
        com.example.pix.payment.v1.SubmitToSPIRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitToSPIMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.pix.payment.v1.ReceiveInboundCreditResponse> receiveInboundCredit(
        com.example.pix.payment.v1.ReceiveInboundCreditRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReceiveInboundCreditMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_PAYMENT = 0;
  private static final int METHODID_GET_PAYMENT = 1;
  private static final int METHODID_LIST_PAYMENTS = 2;
  private static final int METHODID_SUBMIT_TO_SPI = 3;
  private static final int METHODID_RECEIVE_INBOUND_CREDIT = 4;

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
        case METHODID_CREATE_PAYMENT:
          serviceImpl.createPayment((com.example.pix.payment.v1.CreatePaymentRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.payment.v1.CreatePaymentResponse>) responseObserver);
          break;
        case METHODID_GET_PAYMENT:
          serviceImpl.getPayment((com.example.pix.payment.v1.GetPaymentRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.payment.v1.GetPaymentResponse>) responseObserver);
          break;
        case METHODID_LIST_PAYMENTS:
          serviceImpl.listPayments((com.example.pix.payment.v1.ListPaymentsRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.payment.v1.ListPaymentsResponse>) responseObserver);
          break;
        case METHODID_SUBMIT_TO_SPI:
          serviceImpl.submitToSPI((com.example.pix.payment.v1.SubmitToSPIRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.payment.v1.SubmitToSPIResponse>) responseObserver);
          break;
        case METHODID_RECEIVE_INBOUND_CREDIT:
          serviceImpl.receiveInboundCredit((com.example.pix.payment.v1.ReceiveInboundCreditRequest) request,
              (io.grpc.stub.StreamObserver<com.example.pix.payment.v1.ReceiveInboundCreditResponse>) responseObserver);
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
          getCreatePaymentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.payment.v1.CreatePaymentRequest,
              com.example.pix.payment.v1.CreatePaymentResponse>(
                service, METHODID_CREATE_PAYMENT)))
        .addMethod(
          getGetPaymentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.payment.v1.GetPaymentRequest,
              com.example.pix.payment.v1.GetPaymentResponse>(
                service, METHODID_GET_PAYMENT)))
        .addMethod(
          getListPaymentsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.payment.v1.ListPaymentsRequest,
              com.example.pix.payment.v1.ListPaymentsResponse>(
                service, METHODID_LIST_PAYMENTS)))
        .addMethod(
          getSubmitToSPIMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.payment.v1.SubmitToSPIRequest,
              com.example.pix.payment.v1.SubmitToSPIResponse>(
                service, METHODID_SUBMIT_TO_SPI)))
        .addMethod(
          getReceiveInboundCreditMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.pix.payment.v1.ReceiveInboundCreditRequest,
              com.example.pix.payment.v1.ReceiveInboundCreditResponse>(
                service, METHODID_RECEIVE_INBOUND_CREDIT)))
        .build();
  }

  private static abstract class PaymentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PaymentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.pix.payment.v1.PaymentOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PaymentService");
    }
  }

  private static final class PaymentServiceFileDescriptorSupplier
      extends PaymentServiceBaseDescriptorSupplier {
    PaymentServiceFileDescriptorSupplier() {}
  }

  private static final class PaymentServiceMethodDescriptorSupplier
      extends PaymentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PaymentServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (PaymentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PaymentServiceFileDescriptorSupplier())
              .addMethod(getCreatePaymentMethod())
              .addMethod(getGetPaymentMethod())
              .addMethod(getListPaymentsMethod())
              .addMethod(getSubmitToSPIMethod())
              .addMethod(getReceiveInboundCreditMethod())
              .build();
        }
      }
    }
    return result;
  }
}
