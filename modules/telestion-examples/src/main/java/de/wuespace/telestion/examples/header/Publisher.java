package de.wuespace.telestion.examples.header;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.api.verticle.trait.WithTiming;
import io.vertx.core.Vertx;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class Publisher extends TelestionVerticle<Publisher.Configuration> implements WithTiming, WithEventBus {

	public static void main(String[] args) {
		var vertx = Vertx.vertx();

		vertx.deployVerticle(new Publisher());
		vertx.deployVerticle(new Receiver());
	}

	public record Configuration(
			@JsonProperty String outAddress,
			@JsonProperty int delay
	) implements TelestionConfiguration {
		public Configuration() {
			this("publish-channel", 1);
		}
	}

	@Override
	public void onStart() {
		var delay = Duration.ofSeconds(getConfig().delay());
		var counter = new AtomicInteger();

		interval(delay, id -> {
			var infos = new HeaderInformation()
					.add("delay", getConfig().delay())
					.add("counter", counter.getAndIncrement());

			publish(getConfig().outAddress(), "Hello from Publisher", infos);
		});
	}
}
