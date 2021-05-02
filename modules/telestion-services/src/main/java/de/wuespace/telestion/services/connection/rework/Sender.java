package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Sender extends AbstractVerticle {

	@Override
	public void stop(Promise<Void> stopPromise) {
		config = Config.get(config, config(), Configuration.class);
		stopPromise.complete();
	}

	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(config.inputAddress,
				raw -> {
					JsonMessage.on(SenderData.class, raw, this::handleMessage);
					JsonMessage.on(ConnectionData.class, raw, msg -> handleMessage(SenderData.fromConnectionData(msg)));
				});
		startPromise.complete();
	}

	/**
	 * @param inputAddress
	 * @param connectionAddresses
	 */
	public record Configuration(
			@JsonProperty String inputAddress,
			@JsonProperty String... connectionAddresses) implements JsonMessage {

		@SuppressWarnings("unused")
		private Configuration() {
			this(null);
		}
	}

	public Sender() {
		this(null);
	}

	/**
	 *
	 * @param config {@link Configuration} for the creation
	 */
	public Sender(Configuration config) {
		this.config = config;
	}

	/**
	 *
	 * @param msg to send
	 */
	private void handleMessage(SenderData msg) {
		for (var s : config.connectionAddresses()) {
			logger.debug("Sending Message to {}", s);
			vertx.eventBus().publish(s, msg.json());
		}
	}

	/**
	 *
	 */
	private Configuration config;

	/**
	 *
	 */
	private static final Logger logger = LoggerFactory.getLogger(Sender.class);
}
