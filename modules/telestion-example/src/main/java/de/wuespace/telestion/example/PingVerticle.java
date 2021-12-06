package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithEventBus;
import de.wuespace.telestion.deployment.VerticleDeployment;
import de.wuespace.telestion.example.messages.SimpleMessage;
import io.vertx.core.Vertx;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * @author Pablo Klaschka, Ludwig Richter
 */
public class PingVerticle extends TelestionVerticle<PingVerticle.Configuration> implements WithEventBus {

	public static record Configuration(
			@JsonProperty String address,
			@JsonProperty int interval
	) implements TelestionConfiguration {
	}

	public PingVerticle() {
		logger.info("Here is constructor time!");
	}

	public static void main(String[] args) {
		var vertx = Vertx.vertx();

		new VerticleDeployment(vertx)
				.add(PingVerticle.class, new PingVerticle.Configuration("ping-address", 2))
				.add(PongVerticle.class, new PongVerticle.Configuration("ping-address"))
				.deploy()
				.onSuccess(arg -> LoggerFactory.getLogger("main").info("Verticles deployed"));
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
