package org.telestion.widget;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.launcher.Launcher;

import java.time.Duration;

public final class WidgetBridge extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(WidgetBridge.class);

    public static void main(String[] args) {
        Launcher.start(WidgetBridge.class.getName());
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.mountSubRouter("/bridge", bridgeHandler());
        router.route().handler(staticHandler());

        vertx.eventBus().consumer("in", (msg) -> {
            logger.info("Message from client: {}", msg.body());
        });

        vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), timerId -> {
            var message = "Hello from server!";
            logger.info("Sending message to client: {}", message);
            vertx.eventBus().publish("out", message);
        });

        HttpServer http = vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);

        logger.info("Server listening on http://localhost:{}/bridge", http.actualPort());

        startPromise.complete();
    }

    private Router bridgeHandler() {
        SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("in"))
                .addOutboundPermitted(new PermittedOptions().setAddress("out"))
                .addOutboundPermitted(new PermittedOptions().setAddress("out.connected"));

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        return sockJSHandler.bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                logger.info("A socket was created.");
                vertx.eventBus().publish("out.connected", true);
            }

            if (event.type() == BridgeEventType.SEND) {
                logger.info("Client sent message.");
            }
            System.out.println(event.type());
            //We have to complete with "true" for some reason.
            //You can check it in io.vertx.ext.web.handler.sockjs.impl.EventBusBridgeImpl in line 166.
            //I don't know what it means uf you complete with false or null.
            //But in this way it works.
            //Or you just use sockJSHandler.bridge(options) without the events.
            // - jvpichowski
            event.complete(true);
        });
    }

    private StaticHandler staticHandler() {
        return StaticHandler.create()
                .setCachingEnabled(false);
    }
}
