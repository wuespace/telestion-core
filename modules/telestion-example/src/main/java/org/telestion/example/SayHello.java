package org.telestion.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.config.Config;

import java.time.Duration;
import java.util.UUID;

/**
 * A class which says hello and shows the usage of configuration files.
 *
 * @author Jan von Pichowski
 */
public final class SayHello extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(SayHello.class);
	/**
	 * A random uuid which defines this instance
	 */
	private final UUID uuid = UUID.randomUUID();
	/**
	 * The forced configuration defined by the construtor
	 */
	private final Configuration forcedConfig;

	/**
	 * No forced config is used. The config will be read from the config file or the default values will be used if the
	 * config file is not available.
	 */
	public SayHello() {
		this.forcedConfig = null;
	}

	/**
	 * The given forced config is used.
	 *
	 * @param period
	 * @param message
	 */
	public SayHello(long period, String message) {
		this.forcedConfig = new Configuration(period, message);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		var config = Config.get(forcedConfig, config(), Configuration.class);
		vertx.setPeriodic(Duration.ofSeconds(config.period()).toMillis(),
				timerId -> System.out.println(config.message() + " from " + uuid));
		startPromise.complete();
		logger.info("Started {} with config {}", SayHello.class.getSimpleName(), config);
	}

	/**
	 * Define a configuration record
	 */
	@SuppressWarnings("preview")
	private static record Configuration(@JsonProperty long period, @JsonProperty String message) {

		/**
		 * The default values will be set via the constructor
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(1, "Hello World");
		}
	}
}
