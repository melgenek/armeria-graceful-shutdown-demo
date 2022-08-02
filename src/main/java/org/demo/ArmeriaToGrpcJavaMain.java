package org.demo;

import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.Endpoint;
import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.common.Scheme;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class ArmeriaToGrpcJavaMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArmeriaToGrpcJavaMain.class);

    public static class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

        private final CountDownLatch canStop;

        public HelloServiceImpl(CountDownLatch canStop) {
            this.canStop = canStop;
        }

        @Override
        public void hello(Hello.HelloRequest request, StreamObserver<Hello.HelloReply> responseObserver) {
            LOGGER.info("HELLO IN THREAD: " + Thread.currentThread().getName());
            canStop.countDown();
            try {
                Thread.sleep(40000); // 40s is intentional. Smaller sleep, for example, 10s can succeed.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            responseObserver.onNext(Hello.HelloReply.newBuilder().setValue(request.getValue()).build());
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        var canStop = new CountDownLatch(1);
        var server = ServerBuilder.forPort(3700).addService(new HelloServiceImpl(canStop)).build().start();

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
                server.shutdown().awaitTermination();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
