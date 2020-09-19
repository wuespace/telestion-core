package org.telestion.core.connection;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telestion.api.message.JsonMessage;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class TcpConnectionTest {

    @Test
    void roundTripTest(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var serverOutgoing = "serverOutgoing"; //the server publishes all received data to this address on the event bus
        var serverIncoming = "serverIncoming"; //the server sends all on this event bus address consumed data over tcp
        var clientOutgoing = "clientOutgoing"; //the client publishes all received data to this address on the event bus
        var clientIncoming = "clientIncoming"; //the client sends all on this event bus address consumed data over tcp

        vertx.eventBus().consumer(clientOutgoing, msg -> {
            JsonMessage.on(TcpData.class, msg, data -> {
                //send message back
                vertx.eventBus().publish(clientIncoming, data.json());
            });
        });
        vertx.eventBus().consumer(serverOutgoing, msg -> {
            var bytes = new byte[]{2, 4, 5, 3};
            JsonMessage.on(TcpConnected.class, msg, info -> {
                //send message on connection
                vertx.eventBus().publish(serverIncoming, new TcpData(info.host(), info.port(), bytes).json());
            });
            JsonMessage.on(TcpData.class, msg, data -> {
                //test message
                assertThat(data.data(), is(bytes));
                testContext.completeNow();
            });
        });

        var server = new TcpConnection(null, 23654, serverOutgoing, null, Collections.singletonList(serverIncoming));
        var client = new TcpConnection("127.0.0.1", 23654, clientOutgoing, null , Collections.singletonList(clientIncoming));

        vertx.deployVerticle(server, result -> {
            if(result.succeeded()){
                vertx.deployVerticle(client);
            }
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}
