package de.wuespace.telestion.services.recording;

import de.wuespace.telestion.api.verticle.GenericConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

public class Recording extends TelestionVerticle<GenericConfiguration> implements WithEventBus {

	private final ArrayList<String> addresses = new ArrayList<>();

	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(WatchdogActiveAddresses.class, new DeploymentOptions());
		vertx.deployVerticle(Recording.class, new DeploymentOptions());
	}
	private String generateChannelAddress(){
		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		String address = new String(array, StandardCharsets.UTF_8);
		addresses.add(address);
		return address;
	}
	public void onStart() {
		vertx.setPeriodic(1000, id -> {
			vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), timerId -> {
				String address = generateChannelAddress();
				publish(address, "Hello world with trait");
				//vertx.eventBus().publish(address, "Hello World");
				logger.info("Hello World! published on " + address);
				//vertx.eventBus().publish("world", "Hello!");
//				vertx.eventBus().addInboundInterceptor(eb -> {
//
//				});
			});

		});
	}
}
