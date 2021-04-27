package de.wuespace.telestion.core.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.core.message.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.api.config.Config;

public final class DataListener extends AbstractVerticle {
	private final Configuration forcedConfig;
	private Configuration config;

	private final Logger logger = LoggerFactory.getLogger(DataListener.class);

	private final String save = Address.incoming(DataService.class, "save");

	public DataListener() {
		this.forcedConfig = null;
	}

	public DataListener(List<String> listeningAddresses) {
		this.forcedConfig = new Configuration(listeningAddresses);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(forcedConfig, config(), Configuration.class);
		this.registerConsumers();
		startPromise.complete();
	}

	private void registerConsumers() {
		config.listeningAddresses().forEach(address -> {
			vertx.eventBus().consumer(address, document -> {
				JsonMessage.on(JsonMessage.class, document, msg -> {
					vertx.eventBus().publish(save, msg.json());
				});
			});
		});
	}

	private static record Configuration(@JsonProperty List<String> listeningAddresses) {
		private Configuration() {
			this(Collections.emptyList());
		}
	}
}
