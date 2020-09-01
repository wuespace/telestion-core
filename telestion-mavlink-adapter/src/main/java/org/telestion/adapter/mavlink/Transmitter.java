package org.telestion.adapter.mavlink;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
import org.telestion.adapter.mavlink.security.MavV2Signator;
import org.telestion.adapter.mavlink.security.SecretKeySafe;
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
	
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(inAddress, msg -> {
			if (JsonMessage.on(RawMavlink.class, msg, mav -> {
				// Creating Array for CRC-Calc
				byte[] raw = mav.getRaw();
				byte[] buildArray = Arrays.copyOfRange(raw, 1, raw.length -
						(mav instanceof RawMavlinkV2 && ((RawMavlinkV2) mav).incompatFlags() == 0x1 ? 12 : -1));
				
				// Apply Checksum
				Class<? extends MavlinkMessage> clazz = MessageIndex.get(mav instanceof RawMavlinkV2 ?
						((RawMavlinkV2) mav).msgId() :
							((RawMavlinkV1) mav).msgId());
				try {
					Method method = clazz.getMethod("getCrc", Void.class);
					buildArray[buildArray.length-1] = (byte) method.invoke(null, Void.class);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | AnnotationMissingException e) {
					logger.error("Getting CRC_EXTRA of {} failed! Cause:\n{}", clazz.getSimpleName(), e);
					throw new PacketException(new ReflectionException(e));
				}
				int crc = X25Checksum.calculate(buildArray);
				
				// Create new build Array
				buildArray = Arrays.copyOf(raw, raw.length + 2);
				
				buildArray[buildArray.length-2] = (byte) (crc >> 8 & 0xff);
				buildArray[buildArray.length-1]	= (byte) (crc & 0xff);
				
				// Signature only applies for RawMavlinkV2
				if (JsonMessage.on(RawMavlinkV2.class, msg, v2 -> {
					// Apply Signature if requested
					if (v2.incompatFlags() == 0x1) {
						try {
							MavV2Signator.generateSignature(SecretKeySafe.getInstance().getSecretKey(),
									Arrays.copyOfRange(raw, 0, 9), v2.payload().payload(), crc, v2.linkId());
						} catch (NoSuchAlgorithmException e) {
							logger.error("Error while creating signature for MAVLink-Package! Cause:\n{}", e);
							throw new PacketException(e);
						}
					}
				}));
				
				vertx.eventBus().send(outAddress, new MavConnection(buildArray,
						AddressAssociator.remove(mav.getMavlinkId())));
			})) {
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
