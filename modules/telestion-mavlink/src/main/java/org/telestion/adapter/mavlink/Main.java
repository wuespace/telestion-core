package org.telestion.adapter.mavlink;

import java.time.Duration;

import org.telestion.core.connection.TcpConn;
import org.telestion.core.monitoring.MessageLogger;
import org.telestion.launcher.Launcher;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;

public class Main {

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

    public static void main(String[] args) {
        var tcpToReceiver = "tcpToReceiver";
        var receiverToParser = "receiverToParser";
        var parserOut = "parserOut";
        var v1ToRaw = "v1ToRaw";
        var v2ToRaw = "v2ToRaw";
        var parserToTransmitter = "parserToTransmitter";
        var transmitterToTcp = "transmitterToTcp";

        Launcher.start(
                new MessageLogger(),
                new TcpConn(null, 42024, tcpToReceiver, null, null),
                new Receiver(tcpToReceiver, receiverToParser),
                new MavlinkParser(new MavlinkParser.Configuration(
                        receiverToParser, parserOut,
                        v1ToRaw, v2ToRaw, parserToTransmitter
                )),
                new Consumer(),
                new Publisher(42024));
    }

    private static final class Consumer extends AbstractVerticle {
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            vertx.eventBus().consumer("parserOut", msg -> {
                System.out.println(msg.body());
            });
            startPromise.complete();
        }
    }

    private static final class Publisher extends AbstractVerticle {

        private final int port;

        public Publisher(int port) {
            this.port = port;
        }

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            var client = vertx.createNetClient();
            client.connect(port, "localhost", netSocketResult -> {
                if(netSocketResult.failed()){
                    startPromise.fail(netSocketResult.cause());
                    return;
                }
                var socket = netSocketResult.result();
                vertx.setPeriodic(Duration.ofSeconds(3).toMillis(), timedId -> {
                    socket.write(Buffer.buffer(HEARTBEAT_MESSAGE));
                });
            });
            startPromise.complete();
        }
    }
    
}
