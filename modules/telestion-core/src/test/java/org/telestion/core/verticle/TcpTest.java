package org.telestion.core.verticle;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.TcpConnected;
import org.telestion.core.message.TcpData;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class TcpTest {


    @Test
    void test(Vertx vertx, VertxTestContext testContext) throws Throwable {
        vertx.eventBus().consumer(TcpClient.outAddress, msg -> {
            JsonMessage.on(TcpData.class, msg, data -> {
                //send message back
                vertx.eventBus().publish(TcpClient.inAddress, data.json());
            });
        });
        vertx.eventBus().consumer(TcpServer.outAddress, msg -> {
            var bytes = new byte[]{2, 4, 5, 3};
            JsonMessage.on(TcpConnected.class, msg, info -> {
                //send message on connection
                vertx.eventBus().publish(TcpServer.inAddress, new TcpData(info.host(), info.port(), bytes).json());
            });
            JsonMessage.on(TcpData.class, msg, data -> {
                //test message
                assertThat(data.data(), is(bytes));
                testContext.completeNow();
            });
        });

        vertx.deployVerticle(new TcpServer(56565));
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        vertx.deployVerticle(new TcpClient("127.0.0.1", 56565));

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}
