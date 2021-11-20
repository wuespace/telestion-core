package de.wuespace.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.TelestionConfiguration;
import de.wuespace.telestion.api.TelestionVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.time.Duration;

/**
 * A class which says hello and shows the usage of configuration files.
 *
 * @author Jan von Pichowski
 */
public final class SayHello extends TelestionVerticle {
	public static void main(String[] args) throws InterruptedException {
		var vertx = Vertx.vertx();
		System.out.println("Deploying verticle");
		vertx.deployVerticle(SayHello.class, new DeploymentOptions().setConfig(new Configuration(1, "hello world").json()));
		System.out.println("after deploy verticle");
	}

	public static record Configuration(
			@JsonProperty long period,
			@JsonProperty String message
	) implements TelestionConfiguration {
	}

	@Override
	public void onStart(Promise<Void> startPromise) {
		logger.debug("onStart");
		vertx.setPeriodic(Duration.ofSeconds(getGenericConfig().getInteger("period")).toMillis(),
				timerId -> System.out.println(getGenericConfig().getString("message") + " from " + deploymentID()));
		logger.debug("before promise complete");
		startPromise.complete();
		logger.debug("after promise complete");
		logger.info("Started {} with config {}", SayHello.class.getSimpleName(), getConfig());
	}
}
