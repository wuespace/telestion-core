package de.wuespace.telestion.examples.header;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.api.verticle.trait.WithTiming;
import io.vertx.core.Vertx;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class Requester extends TelestionVerticle<Requester.Configuration> implements WithTiming, WithEventBus {

	public static void main(String[] args) {
		var vertx = Vertx.vertx();

		vertx.deployVerticle(new Requester());
		vertx.deployVerticle(new Responder());
	}

	public record Configuration(
			@JsonProperty String requestAddress,
			@JsonProperty int delay
	) implements TelestionConfiguration {
		public Configuration() {
			this("request-channel", 1);
		}
	}

	@Override
	public void onStart() {
		var delay = Duration.ofSeconds(getConfig().delay());
		var requestCounter = new AtomicInteger();

		// Send a "Ping" message with custom headers periodically and request pong signal with custom headers
		interval(delay, id -> {
			var requestTime = System.currentTimeMillis();

			var requestInfos = new DelayCounterInformation(getConfig().delay(), requestCounter.getAndIncrement());
			var requestTimes = new TimeInformation(requestTime, requestTime);

			request(getConfig().requestAddress(), "Ping", requestInfos, requestTimes).onSuccess(message -> {
				var responseInfos = DelayCounterInformation.from(message);
				var responseTimes = TimeInformation.from(message);

				logger.info("Response body: {}", message.body());
				logger.info("Response counter: {}", responseInfos.getCounter());
				logger.info("Message Received on: {}", responseTimes.getReceiveTime());
			});
		});
	}
}
