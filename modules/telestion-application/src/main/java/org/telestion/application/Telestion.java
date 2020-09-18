package org.telestion.application;

import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.JacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.config.Configuration;
import org.telestion.core.config.VerticleConfig;
import org.telestion.launcher.Configurator;

import java.util.Arrays;

public final class Telestion extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Telestion.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Telestion.class.getName());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        //ConfigStoreOptions fileStore = new ConfigStoreOptions()
        //        .setType("file")
        //        .setConfig(new JsonObject().put("path", "conf/config.json"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx);//, new ConfigRetrieverOptions().addStore(fileStore));
        retriever.getConfig(configRes -> {
            if(configRes.failed()){
                logger.error("Failed to load config", configRes.cause());
                throw new RuntimeException(configRes.cause());
            }
            var config = configRes.result().getJsonObject("org.telestion.configuration").mapTo(Configuration.class);
            config.verticles().forEach(verticleConfig -> {
                for (int i = 0; i < verticleConfig.magnitude(); i++) {
                    vertx.deployVerticle(
                            verticleConfig.verticle(),
                            new DeploymentOptions().setConfig(verticleConfig.jsonConfig()));
                }
            });
            System.out.println(config);
        });
        //retriever.listen();

        //DeploymentOptions options = new DeploymentOptions();
        //vertx.deployVerticle(Configurator.class.getName());
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {

    }
}
