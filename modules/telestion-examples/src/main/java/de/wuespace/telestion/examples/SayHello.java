package de.wuespace.telestion.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.GenericConfiguration;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.time.Duration;

/**
 * A class which says hello and shows the usage of configuration files.
 *
 * @author Pablo Klaschka, Jan von Pichowski, Ludwig Richter
 */
public final class SayHello extends TelestionVerticle<GenericConfiguration> {
	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(SayHello.class,
				new DeploymentOptions().setConfig(new Configuration(1, "hello world").json()));
	}

	@Override
	public void onStart(Promise<Void> startPromise) {
		vertx.setPeriodic(Duration.ofSeconds(getGenericConfig().getInteger("period")).toMillis(),
				timerId -> logger.info(
						"{} from {}",
						getGenericConfig().getString("message"),
						deploymentID()
				)
		);
		startPromise.complete();
		logger.info("Started {} with config {}", SayHello.class.getSimpleName(), getConfig());
	}

	public static record Configuration(
			@JsonProperty long period,
			@JsonProperty String message
	) implements TelestionConfiguration {
	}
}
