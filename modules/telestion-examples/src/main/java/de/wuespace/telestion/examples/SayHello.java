package de.wuespace.telestion.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.GenericConfiguration;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import de.wuespace.telestion.api.verticle.trait.WithTiming;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.time.Duration;

/**
 * A class which says hello and shows the usage of configuration files.
 *
 * @author Pablo Klaschka, Jan von Pichowski, Ludwig Richter
 */
public final class SayHello extends TelestionVerticle<SayHello.Configuration> implements WithTiming {
	public static void main(String[] args) {
		var vertx = Vertx.vertx();
		var configuration = new Configuration(1, 10, "hello world");
		vertx.deployVerticle(SayHello.class, new DeploymentOptions().setConfig(configuration.json()));
	}

	public record Configuration(
			@JsonProperty long period,
			@JsonProperty long duration,
			@JsonProperty String message
	) implements TelestionConfiguration {
	}

	@Override
	public void onStart(Promise<Void> startPromise) {
		var delay = Duration.ofSeconds(getConfig().period());
		var duration = Duration.ofSeconds(getConfig().duration());
		// setup interval
		var timing = interval(delay, id -> logger.info(
				"{} from {}",
				getConfig().message(),
				deploymentID()
		));

		// cancel after of some time
		timeout(duration, id -> timing.cancel());

		startPromise.complete();
		logger.info("Started {} with config {}", SayHello.class.getSimpleName(), getConfig());
	}
}
