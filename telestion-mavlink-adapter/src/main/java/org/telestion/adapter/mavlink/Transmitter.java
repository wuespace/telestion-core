package org.telestion.adapter.mavlink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.exception.AnnotationMissingException;
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
			if (msg instanceof RawMavlink mav) {
				if (JsonMessage.on(RawMavlinkV2.class, msg, v2 -> {
					/*
					 * Building MAVLink-Package (Version 2)
					 */
					int index = 0;

					// Creating Array for CRC-Calc
					int length = 10 + v2.payload().payload().length;
					byte[] raw = new byte[length];
									
					raw[index++] = (byte) (v2.len() & 0xff);
					raw[index++] = (byte) (v2.incompatFlags() & 0xff);
					raw[index++] = (byte) (v2.compatFlags() & 0xff);
					raw[index++] = (byte) (v2.seq() & 0xff);
					raw[index++] = (byte) (v2.sysId() & 0xff);
					raw[index++] = (byte) (v2.compId() & 0xff);
					raw[index++] = (byte) ((v2.msgId() >> 16) & 0xff);
					raw[index++] = (byte) ((v2.msgId() >> 8) & 0xff);
					raw[index++] = (byte) (v2.msgId() & 0xff);
					
					for (byte b : v2.payload().payload())
						raw[index++] = b;
					
					// Apply Checksum
					Class<? extends MavlinkMessage> clazz = MessageIndex.get(v2.msgId());
					Method method;
					try {
						method = clazz.getMethod("getCrc", Void.class);
						raw[raw.length-1] = (byte) method.invoke(null, Void.class);
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException | AnnotationMissingException e) {
						logger.error("Getting CRC_EXTRA of {} failed! Cause:\n{}", clazz.getSimpleName(), e);
						throw new PacketException(new ReflectionException(e));
					}
					int crc = X25Checksum.calculate(raw);
					
					// Create new build Array
					length = raw.length + v2.incompatFlags() == 0x1 ? 15 : 2;
					byte[] buildArray = new byte[length];
					
					index = 0;
					buildArray[index++] = (byte) 0xFD;
					
					for (byte b : raw)
						buildArray[index++] = b;
					
					buildArray[index-1] = 	(byte) (crc >> 8 & 0xff);
					buildArray[index++]	= 	(byte) (crc & 0xff);
					
					// Apply Signature if requested
					if (v2.incompatFlags() == 0x1) {
						buildArray[index++] = (byte) v2.linkId();
						for (int i = 5; i > -1; i--)
							buildArray[index++] = (byte) (v2.timestamp() >> (8 * i) & 0xff);
						for (int i = 5; i > -1; i--)
							buildArray[index++] = (byte) (v2.signature() >> (8 * i) & 0xff);
					}
					
					vertx.eventBus().send(outAddress, new MavConnection(buildArray,
							AddressAssociator.remove(v2.getMavlinkId())));
				}));
				else if (JsonMessage.on(RawMavlinkV1.class, msg, v1 -> {
					/*
					 * Building MAVLink-Package (Version 1)
					 */
					int index = 0;

					int length = 6 + v1.payload().payload().length;
					byte[] raw = new byte[length];
									
					raw[index++] = (byte) (v1.len() & 0xff);
					raw[index++] = (byte) (v1.seq() & 0xff);
					raw[index++] = (byte) (v1.sysId() & 0xff);
					raw[index++] = (byte) (v1.compId() & 0xff);
					raw[index++] = (byte) (v1.msgId() & 0xff);
					
					for (byte b : v1.payload().payload())
						raw[index++] = b;
					
					// Apply Checksum
					Class<? extends MavlinkMessage> clazz = MessageIndex.get(v1.msgId());
					Method method;
					try {
						method = clazz.getMethod("getCrc", Void.class);
						raw[raw.length-1] = (byte) method.invoke(null, Void.class);
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException | AnnotationMissingException e) {
						logger.error("Getting CRC_EXTRA of {} failed! Cause:\n{}", clazz.getSimpleName(), e);
						throw new PacketException(new ReflectionException(e));
					}
					int crc = X25Checksum.calculate(raw);
					
					// Create new build Array
					length = raw.length + 2;
					byte[] buildArray = new byte[length];

					index = 0;
					buildArray[index++] = (byte) 0xFE;
					
					for (byte b : raw)
						buildArray[index++] = b;
					
					buildArray[index-1] = 	(byte) (crc >> 8 & 0xff);
					buildArray[index++]	= 	(byte) (crc & 0xff);
					
					vertx.eventBus().send(outAddress, new MavConnection(buildArray,
							AddressAssociator.remove(v1.getMavlinkId())));
				}));
				else
					logger.warn("Unsupported RawMavlink {} sent to {}", mav.getMavlinkId(), msg.address());
			} else {
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
