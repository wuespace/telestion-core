package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.VerticleDeployer;
import de.wuespace.telestion.api.verticle.traits.WithEventBus;
import de.wuespace.telestion.example.messages.SimpleMessage;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

public class PongVerticle extends TelestionVerticle<PongVerticle.Configuration> implements WithEventBus {
	public static record Configuration(
			@JsonProperty String address
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
		register(getConfig().address, this::handlePing, SimpleMessage.class);
	}

	private void handlePing(SimpleMessage body, Message<Object> message) {
		logger.info("Received ping");
		logger.info("Send pong");
		message.reply(new SimpleMessage("pong", "A pong message").json());
	}
}
