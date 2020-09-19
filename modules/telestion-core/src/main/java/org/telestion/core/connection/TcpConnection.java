package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.config.Config;
import org.telestion.core.message.Address;

/**
 *
 */
public final class TcpConnection extends AbstractVerticle {

    private static record Configuration(
            @JsonProperty String host,
            @JsonProperty int port,
            @JsonProperty String outAddress,
            @JsonProperty String inAddress){
        private Configuration(){
            this(null, 7777, Address.outgoing(TcpConnection.class), Address.incoming(TcpConnection.class));
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(TcpConnection.class);

    private final Configuration forcedConfig;
    private Configuration config;
    private NetServer server;
    private NetClient client;

    public TcpConnection(){
        forcedConfig = null;
    }

    public TcpConnection(String host, int port, String outAddress, String inAddress){
        forcedConfig = new Configuration(host, port, outAddress, inAddress);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        config = Config.get(forcedConfig, config(), Configuration.class);

        if(config.host == null){
            //server
            server = vertx.createNetServer(new NetServerOptions().setPort(config.port));
            server.connectHandler(this::onConnected);
            server.exceptionHandler(handler -> logger.error("TCP-Server encountered an unexpected error", handler));
            server.listen(h -> complete(h, startPromise,
                    r -> logger.info("TCP-Server successfully started. Running on port {}", r.actualPort())));
        }else{
            //client
            client = vertx.createNetClient(new NetClientOptions());
            client.connect(config.port, config.host, h -> complete(h, startPromise, this::onConnected));
        }
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        if(server != null){
            server.close(stopPromise);
            return;
        }
        if(client != null){
            client.close();
            stopPromise.complete();
            return;
        }
        stopPromise.complete();
    }

    private void onConnected(NetSocket socket){
        var remoteHost = socket.remoteAddress().host();
        var remotePort = socket.remoteAddress().port();
        logger.info("Connection established ({})", socket.remoteAddress());
        socket.handler(buffer -> vertx.eventBus().publish(config.outAddress(), new TcpData(
                remoteHost, remotePort, buffer.getBytes()).json()));

        socket.closeHandler(handler -> {
            logger.info("Connection closed ({})", socket.remoteAddress());
        });

        vertx.eventBus().consumer(config.inAddress(), msg -> JsonMessage.on(TcpData.class, msg, data -> {
            if(remotePort != data.port() || !remoteHost.equals(data.address())){
                return;
            }
            if(socket.writeQueueFull()){
                logger.error("Write queue of socket is full addr={}, port={}", remoteHost, remotePort);
                return;
            }
            socket.write(Buffer.buffer(data.data()));
        }));
        vertx.eventBus().publish(config.outAddress(), new TcpConnected(remoteHost, remotePort).json());
    }

    private static <T> void complete(AsyncResult<T> result, Promise<?> promise, Handler<T> handler){
        if(result.failed()){
            promise.fail(result.cause());
            return;
        }
        handler.handle(result.result());
        promise.tryComplete();
    }
}
