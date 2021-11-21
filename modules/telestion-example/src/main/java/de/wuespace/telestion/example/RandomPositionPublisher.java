package de.wuespace.telestion.example;

import de.wuespace.telestion.api.TelestionVerticle;
import de.wuespace.telestion.api.traits.WithEventBus;
import de.wuespace.telestion.api.traits.WithSharedData;
import java.time.Duration;
import java.util.Random;

import de.wuespace.telestion.example.messages.Position;
import io.vertx.core.shareddata.LocalMap;
import de.wuespace.telestion.services.message.Address;

/**
 * Test class. <br>
 * Will be removed upon first release.
 */
public final class RandomPositionPublisher extends TelestionVerticle implements WithEventBus, WithSharedData {
	private final Random rand = new Random(555326456);

	@Override
	public void onStart() {
		vertx.setPeriodic(Duration.ofSeconds(3).toMillis(), this::publishPosition);
	}

	/**
	 * Publishes random Position around Kiruna.
	 */
	private void publishPosition(Long timerId) {
		LocalMap<Object, Object> randPos = localMap("randPos");

		var x = (double) randPos.getOrDefault("x", 67.8915);
		var y = (double) randPos.getOrDefault("y", 21.0836);
		var z = (double) randPos.getOrDefault("z", 0.0);

		final Position pos = new Position(x, y, z);

		x += rand.nextDouble() * 0.02;
		y += rand.nextDouble() * 0.02;
		// z += rand.nextDouble()*0.02;
		randPos.put("x", x);
		randPos.put("y", y);
		randPos.put("z", z);

		publish(Address.outgoing(RandomPositionPublisher.class, "MockPos"), pos);
		logger.debug("Sending current pos: {} on {}", pos, RandomPositionPublisher.class.getName());
	}
}
