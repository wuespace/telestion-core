package de.wuespace.telestion.examples;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.time.Duration;

/**
 * A very simple verticle which should present the usage of automatic loading
 * of the default configuration in a Telestion verticle.
 *
 * @author Ludwig Richter (@fussel178)
 */
public class DefaultConfigVerticle extends TelestionVerticle<DefaultConfigVerticle.Configuration> {

	public record Configuration(
			@JsonProperty String message
	) implements TelestionConfiguration {
		// this constructor gets called, when the default configuration is loaded from the base class
		public Configuration() {
			// set some nice defaults
			this("I'm the default message. Feel free to change me! :D");
		}
	}

	public static void main(String[] args) {
		var vertx = Vertx.vertx();

		var customConfiguration = new Configuration("I'm a custom message. Don't change me! XD");
		// deploy with default configuration
		// the base class loads the default configuration on instantiation
		vertx.deployVerticle(DefaultConfigVerticle.class, new DeploymentOptions());
		// deploy with custom configuration (overwrite default configuration)
		vertx.deployVerticle(DefaultConfigVerticle.class, new DeploymentOptions().setConfig(customConfiguration.json()));
	}

	public DefaultConfigVerticle() {
		// uncomment, if you don't want the automatic loading of the default configuration during instantiation
//		super(true);
	}

	@Override
	public void onStart() {
		var delay = Duration.ofSeconds(1).toMillis();
		vertx.setPeriodic(delay, id -> logger.info(getConfig().message()));
	}
}
