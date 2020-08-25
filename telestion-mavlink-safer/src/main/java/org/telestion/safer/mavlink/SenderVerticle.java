package org.telestion.safer.mavlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.json.JSONObject;

import java.time.Duration;

public class SenderVerticle extends AbstractVerticle {
    Logger logger = LoggerFactory.getLogger(SenderVerticle.class.getName());
    @Override
    public void start(){

        vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), (function)->{
            JSONObject position = new JSONObject();

            logger.info("sending message...");
            position.put("x", 5.3);
            position.put("y", 2.8);
            position.put("z", "3.0");
            position.put("name", "Position");
            vertx.eventBus().publish("testAddress", new String(position.toString()));
        });
    }
}
