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
import org.telestion.core.message.TcpData;
import org.telestion.core.verticle.TcpAdapter;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class Receiver extends AbstractVerticle {
	
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(Receiver.class);
	
	/**
	 * 
	 */
	public static final String inAddress = Address.incoming(Receiver.class);
	
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(TcpAdapter.outAddress, msg -> {
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
					vertx.eventBus().send(MavlinkParser.toMavlinkInAddress, mav.json());
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
