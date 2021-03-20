package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.config.Config;
import org.telestion.api.message.JsonMessage;

public final class Receiver extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		for (var con : config.connectionAddresses()) {
			vertx.eventBus().consumer(con,
					raw -> JsonMessage.on(ConnectionData.class, raw,
							msg -> {
								logger.debug("Connection-Message received on {}", con);
								vertx.eventBus().publish(config.outputAddr(), msg);
							}));
		}
		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	/**
	 * @param outputAddr
	 * @param connectionAddresses
	 */
	public record Configuration(
			@JsonProperty String outputAddr,
			@JsonProperty String... connectionAddresses) {

		private Configuration() {
			this(null);
		}
	}

	public Receiver() {
		this(null);
	}

	/**
	 *
	 * @param config {@link Configuration} for the creation
	 */
	public Receiver(Configuration config) {
		this.config = config;
	}

	/**
	 *
	 */
	private Configuration config;

	/**
	 *
	 */
	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

}
