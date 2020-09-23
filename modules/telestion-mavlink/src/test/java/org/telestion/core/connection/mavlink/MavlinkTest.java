package org.telestion.core.connection.mavlink;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telestion.adapter.mavlink.MavlinkParser;
import org.telestion.adapter.mavlink.Receiver;
import org.telestion.adapter.mavlink.message.MessageIndex;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.connection.TcpConn;
import org.telestion.mavlink.messages.mavlink.minimal.Heartbeat;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class MavlinkTest {
    
    @Test
    void testReceiver(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var tcpToReceiver = "tcpToReceiver";
        var receiverToParser = "receiverToParser";
        var parserOut = "parserOut";
        var v1ToRaw = "v1ToRaw";
        var v2ToRaw = "v2ToRaw";
        var parserToTransmitter = "parserToTransmitter";

        MessageIndex.put(new Heartbeat(0L, 0, 0, 0, 0, 0).getId(), Heartbeat.class);

        vertx.deployVerticle(new TcpConn(null, 42024, tcpToReceiver, null, null));
        vertx.deployVerticle(new Receiver(tcpToReceiver, receiverToParser));
        vertx.deployVerticle(new MavlinkParser(new MavlinkParser.Configuration(
                        receiverToParser, parserOut,
                        v1ToRaw, v2ToRaw, parserToTransmitter)));

        vertx.eventBus().consumer(parserOut, msg -> {
            JsonMessage.on(Heartbeat.class, msg, heartbeat -> {
                testContext.completeNow();
            });
        });

        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start(Promise<Void> startPromise) throws Exception {
                var client = vertx.createNetClient();
                client.connect(42024, "127.0.0.1", netSocketResult -> {
                    if(netSocketResult.failed()){
                        startPromise.fail(netSocketResult.cause());
                        return;
                    }
                    var socket = netSocketResult.result();
                    socket.write(Buffer.buffer(HEARTBEAT_MESSAGE));
                });
                startPromise.complete();
            }
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    private static final byte[] HEARTBEAT_MESSAGE = {
            (byte)0xFD,
            (byte)0x09,
            (byte)0x00,
            (byte)0x00,
            (byte)0x00,
            (byte)0x01,
            (byte)0x01,
            (byte)0x00,
            (byte)0x00,
            (byte)0x00,
            (byte)0x09,
            (byte)0x00,
            (byte)0x02,
            (byte)0x00,
            (byte)0x00,
            (byte)0x00,
            (byte)0x00,
            (byte)0x04,
            (byte)0x14,
            (byte)0x54,
            (byte)0x28
    };
}
