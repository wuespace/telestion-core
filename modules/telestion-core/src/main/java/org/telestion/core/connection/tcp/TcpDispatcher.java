package org.telestion.core.connection.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.telestion.api.config.Config;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.connection.ConnectionData;
import org.telestion.core.connection.SenderData;
import org.telestion.core.util.Tuple;

import java.util.Arrays;

public class TcpDispatcher extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		vertx.eventBus().consumer(config.inAddress(), raw -> {
			JsonMessage.on(SenderData.class, raw, msg -> Arrays.stream(msg.conDetails())
					.filter(det -> det instanceof TcpDetails)
					.map(det -> (TcpDetails) det)
					.forEach(det -> {
						for (var server : servers) {
							if (server.isActiveCon(new Tuple<>(det.ip(), det.port()))) {
								vertx.eventBus().publish(server.getConfig().inAddress(),
										new ConnectionData(msg.rawData(), det).json());
							} else {
								vertx.eventBus().publish(config.outAddress(),
										new ConnectionData(msg.rawData(), det).json());
							}
						}
					}));
		});
		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	public record Configuration(@JsonProperty String inAddress,
								@JsonProperty String outAddress) implements JsonMessage {
		/**
		 * For config
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null);
		}
	}

	public TcpDispatcher(TcpServer... servers) {
		this(null, servers);
	}

	public TcpDispatcher(Configuration config, TcpServer... servers) {
		this.config = config;
		this.servers = servers;
	}

	private Configuration config;
	private final TcpServer[] servers;
}
