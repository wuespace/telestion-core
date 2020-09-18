package org.telestion.core.monitoring;

import io.vertx.circuitbreaker.HystrixMetricHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public final class HystrixMetrics extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Create a Vert.x Web router
        Router router = Router.router(vertx);
        // Register the metric handler
        router.get("/hystrix-metrics").handler(HystrixMetricHandler.create(vertx));

        // Create the HTTP server using the router to dispatch the requests
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {

    }
}
