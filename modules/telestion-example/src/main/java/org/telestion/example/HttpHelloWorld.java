package org.telestion.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import org.slf4j.LoggerFactory;

public final class HttpHelloWorld extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(HttpHelloWorld.class.getName());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html");
            response.end(
                """
                <h1>Hello World!</h1>
                """
            );
        });

        server.requestHandler(router).listen(8080);
        LoggerFactory.getLogger(HttpHelloWorld.class).info("Server started!");
        startPromise.complete();
    }
}
