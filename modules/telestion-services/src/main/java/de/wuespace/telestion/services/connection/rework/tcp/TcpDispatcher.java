package de.wuespace.telestion.services.connection.rework.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.connection.rework.ConnectionData;
import de.wuespace.telestion.services.connection.rework.SenderData;
import de.wuespace.telestion.api.Tuple;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import java.util.Arrays;

public class TcpDispatcher extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		vertx.eventBus().consumer(config.inAddress(), raw -> {
			if (!JsonMessage.on(SenderData.class, raw, msg -> Arrays.stream(msg.conDetails())
					.filter(det -> det instanceof TcpDetails)
					.map(det -> (TcpDetails) det)
					.forEach(det -> handle(msg.rawData(), det)))) {
				JsonMessage.on(ConnectionData.class, raw, msg -> {
					if (msg.conDetails() instanceof TcpDetails det) {
						handle(msg.rawData(), det);
					}
				});
			}
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

	private void handle(byte[] bytes, TcpDetails details) {
		for (var server : servers) {
			if (server.isActiveCon(new Tuple<>(details.ip(), details.port()))) {
				vertx.eventBus().publish(server.getConfig().inAddress(),
						new TcpData(bytes, details).json());
			} else {
				vertx.eventBus().publish(config.outAddress(),
						new TcpData(bytes, details).json());
			}
		}
	}

	private Configuration config;
	private final TcpServer[] servers;
}
