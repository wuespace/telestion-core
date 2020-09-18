package org.telestion.application;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.config.Configuration;

import java.util.Collections;

/**
 * This is going to be the Telestion application.
 * It launches the verticles which are specified in the configuration.
 *
 * @author Jan von Pichowski
 */
public final class Telestion extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Telestion.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Telestion.class.getName());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var breaker = CircuitBreaker.create(Telestion.class.getSimpleName(), vertx,
                new CircuitBreakerOptions().setMaxFailures(2).setMaxRetries(5).setTimeout(2000).setResetTimeout(10000));

        var deployer = CircuitBreaker.create(Telestion.class.getSimpleName()+"#deployVerticle", vertx,
                new CircuitBreakerOptions().setMaxRetries(3).setMaxFailures(1).setTimeout(2000).setResetTimeout(10000));

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(configRes -> {
            if(configRes.failed()){
                logger.error("Failed to load config", configRes.cause());
                startPromise.fail(configRes.cause());
                return;
            }
            breaker.execute(future -> {
                var conf = configRes.result().getJsonObject("org.telestion.configuration").mapTo(Configuration.class);
                conf.verticles().stream().flatMap(c -> Collections.nCopies(c.magnitude(), c).stream()).forEach(v -> {
                    logger.info("Deploying {}", v.name());
                    deployer.<String>execute(f -> vertx.deployVerticle(v.verticle(), new DeploymentOptions().setConfig(v.jsonConfig()), f));
                });
                future.complete();
            }, startPromise);
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {

    }
}
