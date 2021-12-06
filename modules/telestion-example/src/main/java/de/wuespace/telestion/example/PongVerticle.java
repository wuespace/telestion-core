package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.deployment.VerticleDeployment;
import de.wuespace.telestion.example.messages.SimpleMessage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pablo Klaschka, Ludwig Richter
 */
public class PongVerticle extends TelestionVerticle<PongVerticle.Configuration> implements WithEventBus {
	public static record Configuration(
			@JsonProperty String address
	) implements TelestionConfiguration {
	}

	public static void main(String[] args) {
		var vertx = Vertx.vertx();

		new VerticleDeployment(vertx)
				.add(PingVerticle.class, new PingVerticle.Configuration("ping-address", 2))
				.add(PongVerticle.class, new PongVerticle.Configuration("ping-address"))
				.deploy()
				.onSuccess(arg -> staticLogger.info("Verticles deployed"));
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

	private static Logger staticLogger = LoggerFactory.getLogger(PongVerticle.class);
}
