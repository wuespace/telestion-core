package org.telestion.widget;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.message.Position;

import java.time.Duration;
import java.util.Random;

public final class WidgetBridge extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(WidgetBridge.class);

    private Random r = new Random(555326456);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.mountSubRouter("/bridge", bridgeHandler());
        router.route().handler(staticHandler());

        HttpServer http = vertx.createHttpServer()
                .requestHandler(router)
                .listen(8081);

        logger.info("Server listening on http://localhost:{}/bridge", http.actualPort());

        vertx.setPeriodic(Duration.ofSeconds(3).toMillis(), timerId -> {
           RandomPositionPublisher(r);
        });

        startPromise.complete();
    }

    private Router bridgeHandler() {
        SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("Frontend/in#position"))
                .addOutboundPermitted(new PermittedOptions().setAddress(WidgetBridge.class.getName()+"/out"+"#MockPos"))
                .addOutboundPermitted(new PermittedOptions().setAddress("current-position"));

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        return sockJSHandler.bridge(options);
    }

    private StaticHandler staticHandler() {
        return StaticHandler.create()
                .setCachingEnabled(false);
    }

    private void RandomPositionPublisher(Random r) {
        Position pos = new Position(
                r.nextDouble()*r.nextInt(10),
                r.nextDouble()*r.nextInt(10),
                r.nextDouble()*r.nextInt(10));
        vertx.eventBus().publish(WidgetBridge.class.getName()+"/out"+"#MockPos",
                JsonObject.mapFrom(pos));
        logger.info("Sending current pos: {} on {}", pos, WidgetBridge.class.getName());
    }
}

/* Further event handling not needed yet, copied to save jvpichowski's comment
*
* return sockJSHandler.bridge(options, event -> {
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
* */
