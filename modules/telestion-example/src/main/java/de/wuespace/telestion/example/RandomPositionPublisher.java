package de.wuespace.telestion.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.time.Duration;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.wuespace.telestion.services.message.Address;

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
		var x = (double) vertx.sharedData().getLocalMap("randPos").getOrDefault("x", 67.8915);
		var y = (double) vertx.sharedData().getLocalMap("randPos").getOrDefault("y", 21.0836);
		var z = (double) vertx.sharedData().getLocalMap("randPos").getOrDefault("z", 0.0);

		final Position pos = new Position(x, y, z);

		x += rand.nextDouble() * 0.02;
		y += rand.nextDouble() * 0.02;
		// z += rand.nextDouble()*0.02;
		vertx.sharedData().getLocalMap("randPos").put("x", x);
		vertx.sharedData().getLocalMap("randPos").put("y", y);
		vertx.sharedData().getLocalMap("randPos").put("z", z);

		vertx.eventBus().publish(Address.outgoing(RandomPositionPublisher.class, "MockPos"), pos.json());
		logger.debug("Sending current pos: {} on {}", pos, RandomPositionPublisher.class.getName());
	}
}
