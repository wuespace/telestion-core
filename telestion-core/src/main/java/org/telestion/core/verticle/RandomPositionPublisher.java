package org.telestion.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.telestion.core.message.Address;
import org.telestion.core.message.Position;

import java.time.Duration;
import java.util.Random;


/**
 * Test class. <br>
 * Will be removed upon first release.
 */
public final class RandomPositionPublisher extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(RandomPositionPublisher.class);
    private final Random rand = new Random(555326456);

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.setPeriodic(Duration.ofSeconds(3).toMillis(), timerId -> publishPosition());
        startPromise.complete();
    }

    /**
     * Publishes random Position around Kiruna.
     */
    private void publishPosition() {
        Position pos = new Position(
                rand.nextDouble()+67.8915,
                rand.nextDouble()+21.0836,
                rand.nextDouble()*0);
        vertx.eventBus().publish(Address.outgoing(RandomPositionPublisher.class, "MockPos"), pos.json());
        logger.debug("Sending current pos: {} on {}", pos, RandomPositionPublisher.class.getName());
    }
}
