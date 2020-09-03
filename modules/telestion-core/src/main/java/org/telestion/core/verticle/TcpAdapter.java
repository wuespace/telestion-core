package org.telestion.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.TcpData;

import java.util.Objects;

public final class TcpAdapter extends AbstractVerticle {

    public static final String outAddress = Address.outgoing(TcpAdapter.class);
    public static final String inAddress = Address.incoming(TcpAdapter.class);

    private static final Logger logger = LoggerFactory.getLogger(TcpAdapter.class);

    private Integer port;
    private NetServer server;

    public TcpAdapter(int port) {
        this.port = port;
    }

    public TcpAdapter() {
        this.port = null;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        logger.info("Starting TCP-Server for MAVLink");

        port = Objects.requireNonNull(config().getInteger("port", port));

        var options = new HttpServerOptions().setPort(port);
        server = vertx.createNetServer(options);

        server.connectHandler(socket -> {
            logger.info("Connection established ({})", socket.remoteAddress());
            socket.handler(buffer -> vertx.eventBus().publish(outAddress, new TcpData(
                    socket.remoteAddress().toString(), port, buffer.getBytes()).json()));

            socket.closeHandler(handler -> {
                logger.info("Connection closed ({})", socket.remoteAddress());
            });

            vertx.eventBus().consumer(inAddress, msg -> JsonMessage.on(TcpData.class, msg, data -> {
                if(port != data.port() || !socket.remoteAddress().toString().equals(data.address())){
                    return;
                }
                if(socket.writeQueueFull()){
                    logger.error("Write queue of socket is full addr={}, port={}", socket.remoteAddress(), port);
                    return;
                }
                socket.write(Buffer.buffer(data.data()));
            }));
        });


        server.exceptionHandler(handler -> {
            logger.error("TCP-Server for MAVLink encountered an unexpected error", handler);
        });

        server.listen(handler -> {
            if(handler.failed()){
                logger.error("Error while starting TCP-Server for MAVLink!", handler.cause());
                startPromise.fail(handler.cause());
                return;
            }
            logger.info("TCP-Server for MAVLink successfully started. Running on port {}", server.actualPort());
        });

        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        server.close(handler -> {
            if(handler.failed()){
                logger.error("Error while stopping TCP-Server for MAVLink!", handler.cause());
                stopPromise.fail(handler.cause());
            }
            logger.info("TCP-Server for MAVLink successfully stopped");
            stopPromise.complete();
        });
        stopPromise.complete();
    }
}
