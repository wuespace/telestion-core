package org.telestion.adapter.mavlink;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.annotation.MavInfo;
import org.telestion.adapter.mavlink.exception.InvalidChecksumException;
import org.telestion.adapter.mavlink.message.MavlinkMessage;
import org.telestion.adapter.mavlink.message.MessageIndex;
import org.telestion.adapter.mavlink.message.internal.RawMavlink;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV1;
import org.telestion.adapter.mavlink.message.internal.RawMavlinkV2;
import org.telestion.adapter.mavlink.security.X25Checksum;
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
				
				Class<? extends MavlinkMessage> mavlinkClass = MessageIndex.get(v2.msgId());
				
				// Check signature and checksum
				if (mavlinkClass.isAnnotationPresent(MavInfo.class)) {
					MavInfo annotation = mavlinkClass.getAnnotation(MavInfo.class);
					int crcExtra = annotation.crc();
					byte[] buildArray = Arrays.copyOfRange(v2.getRaw(), 1, v2.getRaw().length -
												v2.incompatFlags() == 0x1 ? 12 : -1);
					buildArray[buildArray.length-1] = (byte) crcExtra;

					int crc = X25Checksum.calculate(buildArray);
					
					if (crc != v2.checksum()) {
						// TODO: Log those -> @Matei: You need to add a send for logging bad packet!
						throw new InvalidChecksumException("Checksum of received MAVLink-Package was invalid!");
					}
				}
				
				RecordComponent[] components = mavlinkClass.getRecordComponents();
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
			if (JsonMessage.on(MavlinkMessage.class, msg, v2 -> {
				
			})) {
			} else {
				
			}
		});
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
