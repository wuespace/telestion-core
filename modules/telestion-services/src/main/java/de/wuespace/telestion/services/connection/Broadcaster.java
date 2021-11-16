package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Broadcaster extends AbstractVerticle {

	public final static int DEFAULT_ID = 0;

	public final record Configuration(@JsonProperty
									  String inAddress,
									  @JsonProperty
									  int id) implements JsonMessage {
		public Configuration() {
			this(null, DEFAULT_ID);
		}
	}

	@Override
	public void start(Promise<Void> startPromise) {
		this.config = Config.get(this.config, new Configuration(), this.config(), Configuration.class);

		if (broadcasterMap.containsKey(this.config.id())) {
			startPromise.fail("The broadcasters id #%d is already taken!".formatted(this.config.id()));
			return;
		}

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	public static boolean register(int broadcasterId, String address) {
		if (!broadcasterMap.containsKey(broadcasterId)) {
			return false;
		}

		var broadcaster = broadcasterMap.get(broadcasterId);
		broadcaster.addressList.add(address);

		broadcaster.getVertx().eventBus()
				.consumer(broadcaster.config.inAddress(), raw -> {
					if (!JsonMessage.on(RawMessage.class, raw, broadcaster::send)) {
						if (!JsonMessage.on(SenderData.class, raw, broadcaster::send)) {
							JsonMessage.on(ConnectionData.class, raw, broadcaster::send);
						}
					}
				});
		return true;
	}

	public String[] getAddresses() {
		return addressList.toArray(String[]::new);
	}

	public Broadcaster() {
		this(null);
	}

	public Broadcaster(Configuration config) {
		this.config = config;
		this.addressList = new HashSet<>();
	}

	private void send(JsonMessage msg) {
		addressList.forEach(addr -> vertx.eventBus().publish(addr, msg.json()));
	}

	private Configuration config;
	private final Set<String> addressList;

	private static final Map<Integer, Broadcaster> broadcasterMap = new HashMap<>();
}
