package de.wuespace.telestion.example;

import de.wuespace.telestion.api.TelestionVerticle;

import java.time.Duration;

/**
 * Test-Class.<br>
 * Will be removed by the first release.
 */
public final class HelloWorld extends TelestionVerticle {
	@Override
	public void onStart() {
		vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), timerId -> {
			logger.info("Hello World!");
			vertx.eventBus().publish("world", "Hello!");
		});
	}
}
