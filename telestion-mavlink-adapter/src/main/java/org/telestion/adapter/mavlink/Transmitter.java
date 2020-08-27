package org.telestion.adapter.mavlink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.exception.PacketException;
import org.telestion.adapter.mavlink.message.MavlinkMessage;
import org.telestion.adapter.mavlink.message.MessageIndex;
import org.telestion.adapter.mavlink.message.internal.MavConnection;
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
public final class Transmitter extends AbstractVerticle {
	
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(Transmitter.class);
	
	/**
	 * 
	 */
	public static final String outAddress = Address.outgoing(Transmitter.class);
	
	/**
	 * 
	 */
	public static final String inAddress = Address.incoming(Transmitter.class);
	
	@SuppressWarnings("preview")
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(inAddress, msg -> {
			if (JsonMessage.on(RawMavlinkV2.class, msg, v2 -> {
				int length = 10 + v2.payload().payload().length;
				byte[] raw = new byte[length];
				
				raw[0] = (byte) (v2.len() & 0xff);
				raw[1] = (byte) (v2.incompatFlags() & 0xff);
				raw[2] = (byte) (v2.compatFlags() & 0xff);
				raw[3] = (byte) (v2.seq() & 0xff);
				raw[4] = (byte) (v2.sysId() & 0xff);
				raw[5] = (byte) (v2.compId() & 0xff);
				raw[6] = (byte) ((v2.msgId() >> 16) & 0xff);
				raw[7] = (byte) ((v2.msgId() >> 8) & 0xff);
				raw[8] = (byte) (v2.msgId() & 0xff);
				
				int index = 9;
				for (byte b : v2.payload().payload())
					raw[index++] = b;
				
				// Apply Checksum
				Class<? extends MavlinkMessage> clazz = MessageIndex.get(v2.msgId());
				Method method;
				try {
					method = clazz.getMethod("getCrc", Void.class);
					raw[raw.length-1] = (byte) method.invoke(null, Void.class);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					logger.error("Getting the CRC of {} failed! Cause:\n{}", clazz.getSimpleName(), e);
					throw new PacketException(new ReflectionException(e));
				}
				int crc = X25Checksum.calculate(raw);
				
				// Create new build Array
				length = raw.length + v2.incompatFlags() == 0x1 ? 14 : 1;
				byte[] buildArray = new byte[length];
				
				buildArray[0] = (byte) 0xFD;
				index = 1;
				
				for (byte b : raw)
					buildArray[index++] = b;
				
				buildArray[index-1] = (byte) (crc >> 8 & 0xff);
				buildArray[index] = (byte) (crc & 0xff);
				
				// Apply Signature if requested
				if (v2.incompatFlags() == 0x1) {
					
				}
				
				vertx.eventBus().send(AddressAssociator.removeAddress, v2.getMavlinkId());
				// Handle answer
				vertx.eventBus().consumer(AddressAssociator.outAddress, msg2 -> {
					if (msg2.body() instanceof String s) {
						vertx.eventBus().send(outAddress, new MavConnection(buildArray, s));
					} else {
						logger.error("An unsupported datatype ({}) was sent to {}", msg.getClass().getName(),
								msg.address());
					}
				});
			}));
			else if (JsonMessage.on(RawMavlinkV1.class, msg, v1 -> {
				
			}));
			else if (JsonMessage.on(RawMavlink.class, msg, raw -> {
				logger.warn("Unsupported RawMavlink {} sent to {}", raw.getMavlinkId(), msg.address());
			}));
			else {
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
