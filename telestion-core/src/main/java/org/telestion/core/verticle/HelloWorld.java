package org.telestion.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class HelloWorld extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), timerId -> {
            logger.info("Hello World!");
            vertx.eventBus().publish("world", "Hello!");
        });
        startPromise.complete();
    }
}
