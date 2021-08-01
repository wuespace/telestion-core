package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticSender extends AbstractVerticle {

	public record Configuration(@JsonProperty String inAddress,
								@JsonProperty String outAddress,
								@JsonProperty ConnectionDetails staticDetails) implements JsonMessage {
		private Configuration() {
			this(null, null, null);
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		vertx.eventBus().consumer(config.inAddress(), raw -> JsonMessage.on(RawMessage.class, raw, msg -> {
			logger.debug("Sending static message");
			vertx.eventBus().publish(config.outAddress(), new ConnectionData(msg.data(),
					config.staticDetails()).json());
		}));

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	public StaticSender() {
		this(null);
	}

	public StaticSender(Configuration config) {
		this.config = config;
	}

	private Configuration config;
	private final static Logger logger = LoggerFactory.getLogger(StaticSender.class);
}
