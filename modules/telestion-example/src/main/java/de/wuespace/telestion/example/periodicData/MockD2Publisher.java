package de.wuespace.telestion.example.periodicData;

import de.wuespace.telestion.services.database.DataService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.time.Duration;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.wuespace.telestion.services.message.Address;

/**
 * Test class to mock incoming Daedalus2 System_t data from mavlink. <br>
 * Will be removed upon first release.
 */
public final class MockD2Publisher extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(MockD2Publisher.class);
	private final Random rand = new Random(555326456);

	private final String inSave = Address.incoming(DataService.class, "save");

	@Override
	public void start(Promise<Void> startPromise) {
		vertx.setPeriodic(Duration.ofMillis(500).toMillis(), timerId -> publishSystemT());
		startPromise.complete();
	}

	/**
	 * Publishes system_t.
	 */
	private void publishSystemT() {
		System_t system_t = new System_t(
				0L,
				(byte) 0b0,
				.2,
				.4,
				.1,
				.9,
				.87,
				.5,
				.0,
				.0,
				.0,
				.0,
				new byte[]{},
				(byte) 0b0,
				new byte[]{},
				new byte[]{},
				(byte) 0b0,
				.0,
				.0,
				(byte) 0b0,
				(byte) 0b0,
				.0,
				.0,
				.0,
				.0,
				.0,
				.0,
				.0,
				.0,
				.0,
				(byte) 0b0,
				(byte) 0b0
		);
		vertx.eventBus().publish("D2DispatcherInc", system_t.json());
	}
}

