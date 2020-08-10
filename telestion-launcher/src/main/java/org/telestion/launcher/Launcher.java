package org.telestion.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import io.vertx.core.Vertx;

public final class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String... verticleNames){
        Vertx vertx = Vertx.vertx();
        Arrays.stream(verticleNames).forEach(name -> vertx.deployVerticle(name, deployResult -> {
            if (deployResult.failed()) {
                logger.error("Failed to launch verticle {}", name, deployResult.cause());
            }
        }));
    }
}
