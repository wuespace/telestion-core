package org.telestion.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.TcpConnected;
import org.telestion.core.message.TcpData;

import java.util.Objects;

public final class TcpClient extends AbstractVerticle {

    public static final String outAddress = Address.outgoing(TcpClient.class);
    public static final String inAddress = Address.incoming(TcpClient.class);

    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

    private String host;
    private Integer port;
    private NetClient client;

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public TcpClient() {
        this.host = null;
        this.port = null;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        logger.info("Starting TCP-Client");

        port = Objects.requireNonNull(config().getInteger("port", port));
        host = Objects.requireNonNull(config().getString("host", host));

        var options = new NetClientOptions();
        client = vertx.createNetClient(options);

        client.connect(port, host, socketResult -> {
            if(socketResult.failed()){
                logger.error("Failed to start tcp client", socketResult.cause());
                startPromise.fail(socketResult.cause());
                return;
            }
            var socket = socketResult.result();
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

        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        client.close();
        stopPromise.complete();
    }
}