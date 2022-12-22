package de.wuespace.telestion.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.examples.messages.SimpleMessage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.time.Duration;

/**
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 */
public class PingVerticle extends TelestionVerticle<PingVerticle.Configuration> implements WithEventBus {
	public record Configuration(
			@JsonProperty String address,
			@JsonProperty int interval
	) implements TelestionConfiguration {
	}

	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(PingVerticle.class, new DeploymentOptions().setConfig(new PingVerticle.Configuration("ping-address", 2).json()));
		vertx.deployVerticle(PongVerticle.class, new DeploymentOptions().setConfig(new PongVerticle.Configuration("ping-address").json()));

		System.out.println("Verticles deployed");
	}

	@Override
	public void onStart() {
		vertx.setPeriodic(Duration.ofSeconds(getConfig().interval).toMillis(), this::sendPing);
	}

	private void sendPing(Long intervalId) {
		logger.info("Send ping");
		request(getConfig().address, new SimpleMessage("ping", "A simple ping message"),
				SimpleMessage.class).onSuccess(container -> logger.info("Received pong"));
	}
}
