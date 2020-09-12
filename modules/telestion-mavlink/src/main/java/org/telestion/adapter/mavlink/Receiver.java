package org.telestion.adapter.mavlink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.message.internal.RawMavlink;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV1;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV2;
import org.telestion.api.message.JsonMessage;
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
	 * All logs of {@link Receiver} will be using this {@link Logger}.
	 */
	private final Logger logger = LoggerFactory.getLogger(Receiver.class);
	
	/**
	 * Default incoming address for the {@link Receiver}.
	 */
	public static final String inAddress = Address.incoming(Receiver.class);
	
	/**
	 * Can be used to identify multiple {@link MavlinkParsers} for different {@link MavlinkParser}.</br>
	 * It will be added as a suffix to the addresses.
	 */
	private final String addressIdentifier;
	
	/**
	 * Creates a default {@link Receiver} which publishes it's data to the default {@link MavlinkParser}.
	 */
	public Receiver() {
		this("");
	}
	
	/**
	 * Creates a {@link Receiver} which publishes it's data to the {@link MavlinkParser} specified by 
	 * {@link #addressIdentifier}.</br>
	 * Incoming data must be send to the {@link #inAddress} with the suffix {@link #addressIdentifier}.
	 * 
	 * @param identifier specifying where to publish the data
	 */
	public Receiver(String identifier) {
		this.addressIdentifier = identifier;
	}
	
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(TcpServer.outAddress+addressIdentifier, msg -> {
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
					vertx.eventBus().send(MavlinkParser.toMavlinkInAddress+addressIdentifier, mav.json());
				} else {
					logger.warn("TCP-Package with unsupported format received.");
				}
			})) {
				// Might cause problems because sender does not get notified.
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
