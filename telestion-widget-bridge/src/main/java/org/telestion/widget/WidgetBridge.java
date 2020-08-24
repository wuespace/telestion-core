package org.telestion.widget;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.message.Position;
import org.telestion.launcher.Launcher;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class WidgetBridge extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(WidgetBridge.class);

    private Random r = new Random(55532);
    List<Integer> p = r.ints(100).boxed().collect(Collectors.toList());
    private List<Position> MockPos;
    private int cnt = 0;

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.mountSubRouter("/bridge", bridgeHandler());
        router.route().handler(staticHandler());

        HttpServer http = vertx.createHttpServer()
                .requestHandler(router)
                .listen(8081);

        logger.info("Server listening on http://localhost:{}/bridge", http.actualPort());

        Position pos = new Position(2, 3, 5);
        vertx.setPeriodic(Duration.ofSeconds(2).toMillis(), timerId -> {
           logger.info("Sending current pos: { x: 2, y: 3, z: 5 } on {}", WidgetBridge.class.getName());
           vertx.eventBus().publish(WidgetBridge.class.getName()+"/out"+"#MockPos",
                   "{ x: 2, y: 3, z: 5 }");
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
