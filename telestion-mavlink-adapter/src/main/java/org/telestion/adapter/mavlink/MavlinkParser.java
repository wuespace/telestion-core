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

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class MavlinkParser extends AbstractVerticle {
	
	private final Logger logger = LoggerFactory.getLogger(MavlinkParser.class);
	
	public static final String toRawInAddress = Address.incoming(MavlinkParser.class, "toRaw");
	public static final String toRawOutAddress = Address.outgoing(MavlinkParser.class, "toRaw");
	
	public static final String toMavlinkInAddress = Address.incoming(MavlinkParser.class, "toMavlink");
	public static final String toMavlinkOutAddress = Address.incoming(MavlinkParser.class, "toMavlink");
	
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(toMavlinkInAddress, msg -> {
			if (JsonMessage.on(RawMavlinkV2.class, msg, v2 -> {
				System.out.println("Ich lebe v2");
			}));
			else if (JsonMessage.on(RawMavlinkV1.class, msg, v1 -> {
				System.out.println("Ich lebe v1");
			}));
			else if (JsonMessage.on(RawMavlink.class, msg, unsupported -> {
				logger.warn("Unsupported RawMavlink-Package received on {}", msg.address());
			})); else {
				logger.error("Unsupported type sent to {}", msg.address());
			}
		});
		
		vertx.eventBus().consumer(toRawInAddress, msg -> {
			
		});
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
