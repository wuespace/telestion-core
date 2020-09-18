package org.telestion.core.connection;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;

import java.util.Objects;

/**
 * TODO: Add Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes, Jan von Pichowski
 * @version 1.0
 */
public final class TcpServer extends AbstractVerticle {

    public static final String outAddress = Address.outgoing(TcpServer.class);
    public static final String inAddress = Address.incoming(TcpServer.class);

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    private Integer port;
    private NetServer server;

    public TcpServer(int port) {
        this.port = port;
    }

    public TcpServer() {
        this.port = null;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        logger.info("Starting TCP-Server");

        port = Objects.requireNonNull(config().getInteger("port", port));

        var options = new HttpServerOptions().setPort(port);
        server = vertx.createNetServer(options);

        server.connectHandler(socket -> {
            var remoteHost = socket.remoteAddress().host();
            var remotePort = socket.remoteAddress().port();
            logger.info("Connection established ({})", socket.remoteAddress());
            socket.handler(buffer -> vertx.eventBus().publish(outAddress, new TcpData(
                    remoteHost, remotePort, buffer.getBytes()).json()));

            socket.closeHandler(handler -> {
                logger.info("Connection closed ({})", socket.remoteAddress());
            });

            vertx.eventBus().consumer(inAddress, msg -> JsonMessage.on(TcpData.class, msg, data -> {
                if(remotePort != data.port() || !remoteHost.equals(data.address())){
                    return;
                }
                if(socket.writeQueueFull()){
                    logger.error("Write queue of socket is full addr={}, port={}", remoteHost, remotePort);
                    return;
                }
                socket.write(Buffer.buffer(data.data()));
            }));
            vertx.eventBus().publish(outAddress, new TcpConnected(remoteHost, remotePort).json());
        });

        server.exceptionHandler(handler -> {
            logger.error("TCP-Server encountered an unexpected error", handler);
        });

        server.listen(handler -> {
            if(handler.failed()){
                logger.error("Error while starting TCP-Server!", handler.cause());
                startPromise.fail(handler.cause());
                return;
            }
            logger.info("TCP-Server successfully started. Running on port {}", server.actualPort());
            startPromise.complete();
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        server.close(handler -> {
            if(handler.failed()){
                logger.error("Error while stopping TCP-Server!", handler.cause());
                stopPromise.fail(handler.cause());
                return;
            }
            logger.info("TCP-Server successfully stopped");
            stopPromise.complete();
        });
    }
}
