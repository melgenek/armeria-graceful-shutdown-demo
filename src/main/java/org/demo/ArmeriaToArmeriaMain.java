package org.demo;

import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.Endpoint;
import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.common.Scheme;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerPort;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.grpc.GrpcService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class ArmeriaToArmeriaMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArmeriaToArmeriaMain.class);

    public static class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

        private final CountDownLatch canStop;

        public HelloServiceImpl(CountDownLatch canStop) {
            this.canStop = canStop;
        }

        @Override
        public void hello(Hello.HelloRequest request, StreamObserver<Hello.HelloReply> responseObserver) {
            ServiceRequestContext.current().blockingTaskExecutor().submit(() -> {
                LOGGER.info("HELLO IN THREAD: " + Thread.currentThread().getName());
                canStop.countDown();
                try {
                    Thread.sleep(40000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                responseObserver.onNext(Hello.HelloReply.newBuilder().setValue(request.getValue()).build());
                responseObserver.onCompleted();
            });
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var canStop = new CountDownLatch(1);

        var server = Server.builder()
                .port(new ServerPort(new InetSocketAddress("127.0.0.1", 3700), SessionProtocol.HTTP))
                .service(GrpcService.builder().addService(new HelloServiceImpl(canStop)).build())
                .gracefulShutdownTimeout(Duration.ZERO, Duration.ofDays(1))
                .connectionDrainDuration(Duration.ofDays(1))
                .build();
        server.start().join();

        var client = GrpcClients
                .builder(
                        Scheme.of(GrpcSerializationFormats.PROTO, SessionProtocol.H2C),
                        Endpoint.of("127.0.0.1", 3700)
                )
                .responseTimeout(Duration.ofSeconds(120))
                .factory(ClientFactory.builder().tlsNoVerify().build())
                .build(HelloServiceGrpc.HelloServiceFutureStub.class);

        stopServerAfterBeforeTheRequestEnd(canStop, server);

        LOGGER.info("SENDING");
        var responseFuture = client.hello(Hello.HelloRequest.newBuilder().setValue("hello").build());
        LOGGER.info("RECEIVED: " + responseFuture.get());
    }

    private static void stopServerAfterBeforeTheRequestEnd(CountDownLatch canStop, Server server) {
        new Thread(() -> {
            try {
                canStop.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            server.stop().join();
        }).start();
    }

}
