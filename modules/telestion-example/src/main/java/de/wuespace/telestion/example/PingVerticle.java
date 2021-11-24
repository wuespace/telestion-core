package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.VerticleDeployer;
import de.wuespace.telestion.api.verticle.traits.WithEventBus;
import de.wuespace.telestion.example.messages.SimpleMessage;
import io.vertx.core.Vertx;

import java.time.Duration;

public class PingVerticle extends TelestionVerticle<PingVerticle.Configuration> implements WithEventBus {
	public static record Configuration(
			@JsonProperty String address,
			@JsonProperty int interval
	) implements TelestionConfiguration {
	}

	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		VerticleDeployer.deploy(vertx, PingVerticle.class, new PingVerticle.Configuration("ping-address", 2));
		VerticleDeployer.deploy(vertx, PongVerticle.class, new PongVerticle.Configuration("ping-address"));
		System.out.println("Verticles deployed");
	}

	@Override
	public void onStart() {
		vertx.setPeriodic(Duration.ofSeconds(getConfig().interval).toMillis(), this::sendPing);
	}

	private void sendPing(Long intervalId) {
		logger.info("Send ping");
		request(getConfig().address, new SimpleMessage("ping", "A simple ping message"), SimpleMessage.class).onSuccess(container -> logger.info("Received pong"));
	}
}
