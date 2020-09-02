package org.telestion.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A simple WebServer which publishes the index page.
 *
 * @author Jan von Pichowski
 */
public final class WebServer extends AbstractVerticle {

    private Integer port;

    /**
     *
     * @param port the port to bind to
     */
    public WebServer(int port) {
        this.port = port;
    }

    public WebServer() {
        this.port = null;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        port = Objects.requireNonNull(context.config().getInteger("port", port));

        var data = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("index.html")).readAllBytes();
        var content = new String(data, StandardCharsets.UTF_8);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html");
            response.end(content);
        });

        server.requestHandler(router).listen(port);
    }
}
