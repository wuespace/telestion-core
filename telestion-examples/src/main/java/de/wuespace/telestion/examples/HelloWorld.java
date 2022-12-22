package de.wuespace.telestion.examples;

import de.wuespace.telestion.api.verticle.NoConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;

import java.time.Duration;

/**
 * Test-Class.<br>
 * Will be removed by the first release.
 *
 * @author Jan von Pichowski (@jvpichowski)
 */
public final class HelloWorld extends TelestionVerticle<NoConfiguration> {
	@Override
	public void onStart() {
		vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), timerId -> {
			logger.info("Hello World!");
			vertx.eventBus().publish("world", "Hello!");
		});
	}
}
