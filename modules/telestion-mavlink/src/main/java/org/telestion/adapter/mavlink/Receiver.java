package org.telestion.adapter.mavlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.message.internal.RawMavlink;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV1;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV2;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.config.Config;
import org.telestion.core.message.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;

import org.telestion.core.message.TcpData;
import org.telestion.core.verticle.TcpServer;

/**
 * {@link Verticle} which handles incoming MAVLink-Messages (in bytes[]).</br>
 * Incoming messages will be parsed into a {@link RawMavlink} and send to the {@link MavlinkParser}.
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class Receiver extends AbstractVerticle {

	/**
	 * The configuration of the receiver
	 *
	 * @param dataProviderAddress the address which provides the data which should be received
	 * @param rawMavConsumerAddress the address which consumes the RawMavlink message
	 */
	private static record Configuration(
			@JsonProperty String dataProviderAddress,
			@JsonProperty String rawMavConsumerAddress){

		@SuppressWarnings("unused")
		private Configuration() {
			this(Address.incoming(Receiver.class), Address.outgoing(Receiver.class));
		}
	}

	/**
	 * All logs of {@link Receiver} will be using this {@link Logger}.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

	private final Configuration forcedConfig;
	
	/**
	 * Creates a default {@link Receiver} which publishes it's data to the specified addresses.
	 * The addresses are either defined by the default configuration ore in the config file.
	 * The default configuration is dataProviderAddress=Address.incoming(Receiver.class) and
	 * rawMavConsumerAddress=Address.outgoing(Receiver.class).
	 */
	public Receiver() {
		forcedConfig = null;
	}
	
	/**
	 * Creates a {@link Receiver} which publishes it's data to the given addresses.
	 *
	 * @param dataProviderAddress the address which provides the data which should be received
	 * @param rawMavConsumerAddress the address which consumes the RawMavlink message
	 */
	public Receiver(String dataProviderAddress, String rawMavConsumerAddress) {
		forcedConfig = new Configuration(dataProviderAddress, rawMavConsumerAddress);
	}
	
	@Override
	public void start(Promise<Void> startPromise) {
		var config = Config.get(forcedConfig, config(), Configuration.class);

		vertx.eventBus().consumer(config.dataProviderAddress(), msg -> {
			if (!JsonMessage.on(TcpData.class, msg, data -> {
				byte[] bytes = data.data();

				RawMavlink mav = switch(bytes[0]) {
						case (byte) 0xFD -> (bytes.length > 11
								? new RawMavlinkV2(bytes)
								: null);
						case (byte) 0xFE -> (bytes.length > 7
								? new RawMavlinkV1(bytes)
								: null);
						default -> null;
				};
				
				if (mav != null) {
					AddressAssociator.put(mav.getMavlinkId(), new AddressPort(data.address(), data.port()));
					vertx.eventBus().send(config.rawMavConsumerAddress(), mav.json());
				} else {
					logger.warn("TCP-Package with unsupported format received.");
				}
			})) {
				// Might cause problems because sender does not get notified.
				// TODO forward to mavlink-safer
				logger.error("Unsupported type sent to {}", msg.address());
			}
		});
		
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
