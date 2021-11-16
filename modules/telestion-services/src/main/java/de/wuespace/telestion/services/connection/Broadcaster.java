package de.wuespace.telestion.services.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Broadcaster extends AbstractVerticle {

	public final static int NO_BROADCASTING = -1;
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

		broadcasterMap.put(this.config.id(), this);

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	/**
	 * Registers a new address for the broadcaster with the given id if it was previously specified in the config.
	 *
	 * @param broadcasterId		0 = default broadcaster, must be >= 0
	 * @param address			Address on VertX bus it must be sent to
	 * @return		if registration process was successful
	 */
	public static boolean register(int broadcasterId, String address) {
		if (broadcasterId == NO_BROADCASTING) {
			return true;
		}

		if (!broadcasterMap.containsKey(broadcasterId) || broadcasterId < 0) {
			logger.error("Setup invalid!");
			return false;
		}

		var broadcaster = broadcasterMap.get(broadcasterId);
		broadcaster.addressList.add(address);

		broadcaster.getVertx().eventBus()
				.consumer(broadcaster.config.inAddress(), raw -> {
					if (!JsonMessage.on(RawMessage.class, raw, broadcaster::send)) {
						if (!JsonMessage.on(SenderData.class,
								raw,
								msg -> broadcaster.send(new RawMessage(msg.rawData())))) {
							JsonMessage.on(ConnectionData.class,
									raw,
									msg -> broadcaster.send(new RawMessage(msg.rawData())));
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

	private void send(RawMessage msg) {
		addressList.forEach(addr -> vertx.eventBus().publish(addr, msg.json()));
	}

	private Configuration config;
	private final Set<String> addressList;

	private static final Map<Integer, Broadcaster> broadcasterMap = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(Broadcaster.class);
}
