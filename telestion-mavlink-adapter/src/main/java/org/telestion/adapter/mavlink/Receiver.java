package org.telestion.adapter.mavlink;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.message.internal.AddressMapping;
import org.telestion.adapter.mavlink.message.internal.MavConnection;
import org.telestion.adapter.mavlink.message.internal.RawMavlink;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV1;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV2;
import org.telestion.adapter.mavlink.message.internal.RawPayload;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

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
		vertx.eventBus().consumer(inAddress, msg -> {
			if (JsonMessage.on(MavConnection.class, msg, con -> {
				byte[] bytes = con.bytes();
				
				// bytes[]-Length will not be checked. If invalid an exception will be thrown!
				RawMavlink mav = switch(bytes[0]) {
						case (byte) 0xFD -> (bytes.length > 10
								? new RawMavlinkV2(
									(short) bytes[1],
									(short) bytes[2],
									(short) bytes[3],
									(short) bytes[4],
									(short) bytes[5],
									(short) bytes[6],
									(long) bytes[7] << 16 + (bytes[8] << 8) + bytes[9],
									new RawPayload(Arrays.copyOfRange(bytes, 10, bytes[1] + 10)),
									(int) (bytes[bytes[1] + 11] << 8) + bytes[bytes[1] + 12],
									(bytes.length > bytes[1] + 13
											? Arrays.copyOfRange(bytes, bytes[1] + 13, bytes[1] + 26)
											: null))
								: null);
						case (byte) 0xFE -> (bytes.length > 7
								? new RawMavlinkV1(
									(short) bytes[1],
									(short) bytes[2],
									(short) bytes[3],
									(short) bytes[4],
									(short) bytes[5],
									new RawPayload(Arrays.copyOfRange(bytes, 6, bytes[1] + 6)),
									(int) (bytes[bytes[1] + 6] << 8) + bytes[bytes[1] + 7])
								: null);
						default -> null;
				};
				
				if (mav != null) {
					vertx.eventBus().send(AddressAssociator.putAddress,
							new AddressMapping(mav.getMavlinkId(), con.remoteAddress()).json());
					vertx.eventBus().send(MavlinkParser.toMavlinkInAddress, mav.json());
				} else {
					logger.warn("TCP-Package with unsupported format received.");
				}
			}));
			else {
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
