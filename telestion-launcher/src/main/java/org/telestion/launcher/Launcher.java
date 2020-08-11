package org.telestion.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import io.vertx.core.Vertx;

/**
 * A generic launcher class which deploys verticles.
 */
public final class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    /**
     * Deploys the given verticles.
     * If Vert.x fails to deploy a verticle, it will retry 5 secs later again.
     * 
     * @param args the class names of the verticles which should be deployed.
     */
    public static void main(String[] args) {
        start(args);
    }

    /**
     * Deploys the given verticles.
     * If Vert.x fails to deploy a verticle, it will retry 5 secs later again.
     *
     * @param verticleNames the class names of the verticles which should be deployed.
     */
    public static void start(String... verticleNames){
        logger.info("Deploying {} verticles", verticleNames.length);
        var vertx = Vertx.vertx();
        Arrays.stream(verticleNames).forEach(verticleName -> {
            logger.info("Deploying verticle {}", verticleName);
            vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), timerId -> {
                vertx.deployVerticle(verticleName, res -> {
                    if(res.failed()){
                        logger.error("Failed to deploy verticle {} retrying in 5s", verticleName, res.cause());
                        return;
                    }
                    logger.info("Deployed verticle {} with id {}", verticleName, res.result());
                    vertx.cancelTimer(timerId);
                });
            });
        });
    }
}
