### Armeria gRPC graceful shutdown comparison

This repository compares the behaviour of the graceful shutdown using the Armeria gRPC client.
The behaviour differs between Armeria gRPC server and Java gRPC server.

#### (Succeeds) Armeria client. Armeria server:
```
12:07:59,817 [main] INFO org.demo.ArmeriaToArmeriaMain - SENDING
12:07:59,836 [main] INFO com.linecorp.armeria.internal.common.JavaVersionSpecific - Using the APIs optimized for: Java 9+
12:07:59,954 [armeria-common-worker-nio-2-3] DEBUG com.linecorp.armeria.server.HttpServerHandler - [id: 0x65f5cafa, L:/127.0.0.1:3700 - R:/127.0.0.1:54629] HTTP/2 settings: {ENABLE_PUSH=0, INITIAL_WINDOW_SIZE=1048576, MAX_HEADER_LIST_SIZE=8192}
12:08:00,001 [armeria-common-blocking-tasks-1-1] INFO org.demo.ArmeriaToArmeriaMain - HELLO IN THREAD: armeria-common-blocking-tasks-1-1
12:08:00,005 [armeria-common-worker-nio-2-3] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0x65f5cafa, L:/127.0.0.1:3700 - R:/127.0.0.1:54629] Sent a GOAWAY frame: lastStreamId=2147483647, errorCode=NO_ERROR(0)
12:08:00,007 [armeria-common-worker-nio-2-2] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0xdd5d61d2, L:/127.0.0.1:54629 - R:127.0.0.1/127.0.0.1:3700] Received a GOAWAY frame: lastStreamId=2147483647, errorCode=NO_ERROR(0)
12:08:40,035 [main] INFO org.demo.ArmeriaToArmeriaMain - RECEIVED: value: "hello"

12:08:50,030 [armeria-common-worker-nio-2-2] DEBUG com.linecorp.armeria.internal.common.AbstractKeepAliveHandler - [id: 0xdd5d61d2, L:/127.0.0.1:54629 - R:127.0.0.1/127.0.0.1:3700] Closing an idle client connection
12:08:50,031 [armeria-common-worker-nio-2-2] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0xdd5d61d2, L:/127.0.0.1:54629 - R:127.0.0.1/127.0.0.1:3700] Sent a GOAWAY frame: lastStreamId=0, errorCode=NO_ERROR(0)
12:08:50,032 [armeria-common-worker-nio-2-3] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0x65f5cafa, L:/127.0.0.1:3700 - R:/127.0.0.1:54629] Received a GOAWAY frame: lastStreamId=0, errorCode=NO_ERROR(0)
12:08:51,042 [Thread-0] DEBUG com.linecorp.armeria.client.ClientFactory - Closing the default client factories
12:08:51,048 [Thread-0] DEBUG com.linecorp.armeria.client.ClientFactory - Closed the default client factories
```

#### (Fails) Armeria client. Grpc Java server:
```
12:02:57,159 [main] INFO org.demo.ArmeriaToGrpcJavaMain - SENDING
12:02:57,177 [main] INFO com.linecorp.armeria.internal.common.JavaVersionSpecific - Using the APIs optimized for: Java 9+
12:02:57,298 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND SETTINGS: ack=false settings={MAX_CONCURRENT_STREAMS=2147483647, INITIAL_WINDOW_SIZE=1048576, MAX_HEADER_LIST_SIZE=8192}
12:02:57,299 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND WINDOW_UPDATE: streamId=0 windowSizeIncrement=983041
12:02:57,304 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND SETTINGS: ack=false settings={ENABLE_PUSH=0, INITIAL_WINDOW_SIZE=1048576, MAX_HEADER_LIST_SIZE=8192}
12:02:57,304 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND SETTINGS: ack=true
12:02:57,304 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND WINDOW_UPDATE: streamId=0 windowSizeIncrement=983041
12:02:57,304 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND SETTINGS: ack=true
12:02:57,319 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND HEADERS: streamId=3 headers=GrpcHttp2RequestHeaders[:path: /demo.org.HelloService/Hello, :authority: 127.0.0.1:3700, :method: POST, :scheme: http, te: trailers, content-type: application/grpc+proto, grpc-accept-encoding: gzip, grpc-timeout: 60000000u, content-length: 12, user-agent: armeria/1.17.1] padding=0 endStream=false
12:02:57,332 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND DATA: streamId=3 padding=0 endStream=true length=12 bytes=00000000070a0568656c6c6f
12:02:57,332 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND PING: ack=false bytes=1234
12:02:57,333 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND PING: ack=true bytes=1234
12:02:57,342 [grpc-default-executor-0] INFO org.demo.ArmeriaToGrpcJavaMain - HELLO IN THREAD: grpc-default-executor-0
12:02:57,346 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND GO_AWAY: lastStreamId=2147483647 errorCode=0 length=13 bytes=6170705f726571756573746564
12:02:57,347 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND PING: ack=false bytes=40715087873
12:02:57,347 [armeria-common-worker-nio-5-2] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0x56055957, L:/127.0.0.1:54515 - R:127.0.0.1/127.0.0.1:3700] Received a GOAWAY frame: lastStreamId=2147483647, errorCode=NO_ERROR(0)
12:02:57,348 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND PING: ack=true bytes=40715087873
12:02:57,348 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] OUTBOUND GO_AWAY: lastStreamId=3 errorCode=0 length=13 bytes=6170705f726571756573746564
12:02:57,348 [armeria-common-worker-nio-5-2] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0x56055957, L:/127.0.0.1:54515 - R:127.0.0.1/127.0.0.1:3700] Received a GOAWAY frame: lastStreamId=3, errorCode=NO_ERROR(0)
12:02:57,348 [armeria-common-worker-nio-5-2] DEBUG com.linecorp.armeria.internal.common.Http2GoAwayHandler - [id: 0x56055957, L:/127.0.0.1:54515 - R:127.0.0.1/127.0.0.1:3700] Sent a GOAWAY frame: lastStreamId=0, errorCode=NO_ERROR(0)
12:02:57,349 [grpc-nio-worker-ELG-3-1] DEBUG io.grpc.netty.NettyServerHandler - [id: 0x021300a8, L:/127.0.0.1:3700 - R:/127.0.0.1:54515] INBOUND GO_AWAY: lastStreamId=0 errorCode=0 length=0 bytes=
Exception in thread "main" java.util.concurrent.ExecutionException: io.grpc.StatusRuntimeException: UNKNOWN
	at com.google.common.util.concurrent.AbstractFuture.getDoneValue(AbstractFuture.java:588)
	at com.google.common.util.concurrent.AbstractFuture.get(AbstractFuture.java:567)
	at org.demo.ArmeriaToGrpcJavaMain.main(ArmeriaToGrpcJavaMain.java:70)
Caused by: io.grpc.StatusRuntimeException: UNKNOWN
	at io.grpc.Status.asRuntimeException(Status.java:535)
	at io.grpc.stub.ClientCalls$UnaryStreamToFuture.onClose(ClientCalls.java:542)
	at com.linecorp.armeria.internal.client.grpc.ArmeriaClientCall.close(ArmeriaClientCall.java:499)
	at com.linecorp.armeria.internal.client.grpc.ArmeriaClientCall.transportReportStatus(ArmeriaClientCall.java:443)
	at com.linecorp.armeria.internal.common.grpc.HttpStreamDeframer.processOnError(HttpStreamDeframer.java:136)
	at com.linecorp.armeria.internal.common.stream.DecodedStreamMessage$DecodingSubscriber.onError(DecodedStreamMessage.java:274)
	at com.linecorp.armeria.common.stream.AbstractStreamMessage$CloseEvent.notifySubscriber(AbstractStreamMessage.java:269)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.notifySubscriberOfCloseEvent0(DefaultStreamMessage.java:300)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.notifySubscriberOfCloseEvent(DefaultStreamMessage.java:292)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.handleCloseEvent(DefaultStreamMessage.java:429)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.notifySubscriber0(DefaultStreamMessage.java:372)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.notifySubscriber(DefaultStreamMessage.java:328)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.addObjectOrEvent(DefaultStreamMessage.java:314)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.tryClose(DefaultStreamMessage.java:458)
	at com.linecorp.armeria.common.stream.DefaultStreamMessage.close(DefaultStreamMessage.java:447)
	at com.linecorp.armeria.client.HttpResponseDecoder$HttpResponseWrapper.closeAction(HttpResponseDecoder.java:330)
	at com.linecorp.armeria.client.HttpResponseDecoder$HttpResponseWrapper.cancelTimeoutOrLog(HttpResponseDecoder.java:370)
	at com.linecorp.armeria.client.HttpResponseDecoder$HttpResponseWrapper.close(HttpResponseDecoder.java:318)
	at com.linecorp.armeria.client.HttpResponseDecoder$HttpResponseWrapper.close(HttpResponseDecoder.java:313)
	at com.linecorp.armeria.client.HttpResponseDecoder.failUnfinishedResponses(HttpResponseDecoder.java:139)
	at com.linecorp.armeria.client.HttpSessionHandler.channelInactive(HttpSessionHandler.java:424)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:241)
	at io.netty.handler.codec.ByteToMessageDecoder.channelInputClosed(ByteToMessageDecoder.java:392)
	at io.netty.handler.codec.ByteToMessageDecoder.channelInactive(ByteToMessageDecoder.java:357)
	at io.netty.handler.codec.http2.Http2ConnectionHandler.channelInactive(Http2ConnectionHandler.java:430)
	at com.linecorp.armeria.client.Http2ClientConnectionHandler.channelInactive(Http2ClientConnectionHandler.java:128)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:241)
	at io.netty.handler.logging.LoggingHandler.channelInactive(LoggingHandler.java:206)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:241)
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelInactive(DefaultChannelPipeline.java:1405)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248)
	at io.netty.channel.DefaultChannelPipeline.fireChannelInactive(DefaultChannelPipeline.java:901)
	at io.netty.channel.AbstractChannel$AbstractUnsafe$7.run(AbstractChannel.java:813)
	at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:174)
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:167)
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:470)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:503)
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: com.linecorp.armeria.common.ClosedSessionException
	at com.linecorp.armeria.common.ClosedSessionException.get(ClosedSessionException.java:35)
	at com.linecorp.armeria.client.HttpSessionHandler.getPendingException(HttpSessionHandler.java:461)
	at com.linecorp.armeria.client.HttpSessionHandler.channelInactive(HttpSessionHandler.java:423)
	... 27 more
12:03:27,359 [Thread-0] DEBUG com.linecorp.armeria.client.ClientFactory - Closing the default client factories
12:03:27,364 [Thread-0] DEBUG com.linecorp.armeria.client.ClientFactory - Closed the default client factories
```