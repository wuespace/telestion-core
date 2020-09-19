package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.config.Config;
import org.telestion.core.message.Address;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class TcpConnection extends AbstractVerticle {

    /**
     * @param host the host to which the connection should be established or null if this is the host of the connection
     * @param port the port of the connection host
     * @param broadcastAddress the address to which the incoming data should be published or null if no publishing is allowed
     * @param targetAddresses the list of addresses to which the incoming data should be send or null if no direct targets exist
     * @param consumingAddresses the list of addresses from which data will be consumed
     */
    private static record Configuration(
            @JsonProperty String host,
            @JsonProperty int port,
            @JsonProperty String broadcastAddress,
            @JsonProperty List<String> targetAddresses,
            @JsonProperty List<String> consumingAddresses){
        private Configuration(){
            this(null, 7777,
                    Address.outgoing(TcpConnection.class),
                    null,
                    Collections.singletonList(Address.incoming(TcpConnection.class)));
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

    public TcpConnection(String host, int port,
                         String broadcastAddress, List<String> targetAddresses, List<String> consumingAddresses){
        forcedConfig = new Configuration(host, port, broadcastAddress, targetAddresses, consumingAddresses);
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
        socket.handler(buffer -> out(new TcpData(remoteHost, remotePort, buffer.getBytes()).json()));

        socket.closeHandler(handler -> logger.info("Connection closed ({})", socket.remoteAddress()));

        consume(msg -> JsonMessage.on(TcpData.class, msg, data -> {
            if(remotePort != data.port() || !remoteHost.equals(data.address())){
                return;
            }
            if(socket.writeQueueFull()){
                logger.error("Write queue of socket is full addr={}, port={}", remoteHost, remotePort);
                return;
            }
            socket.write(Buffer.buffer(data.data()));
        }));

        out(new TcpConnected(remoteHost, remotePort).json());
    }

    private <T> void consume(Handler<Message<T>> handler){
        if(config.consumingAddresses() != null){
            config.consumingAddresses().forEach(addr -> {
                vertx.eventBus().consumer(addr, handler);
            });
        }
    }

    private void out(Object data){
        if(config.broadcastAddress() != null) {
            vertx.eventBus().publish(config.broadcastAddress(), data);
        }
        if(config.targetAddresses() != null){
            config.targetAddresses().forEach(addr -> vertx.eventBus().send(addr, data));
        }
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
