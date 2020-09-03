package org.telestion.adapter.mavlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import org.telestion.adapter.mavlink.message.Heartbeat;
import org.telestion.core.verticle.TcpAdapter;
import org.telestion.launcher.Launcher;

import java.time.Duration;

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
    	new Heartbeat(0, 0, 0, 0, 0, 0);
        Launcher.start(
                new TcpAdapter(42024),
                new Receiver(),
                new MavlinkParser(),
                new Consumer(),
                new Publisher(42024));
    }

    private static final class Consumer extends AbstractVerticle {
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            vertx.eventBus().consumer(MavlinkParser.toMavlinkOutAddress , msg -> {
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
