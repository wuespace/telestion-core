package org.telestion.adapter.mavlink;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.annotation.MavArray;
import org.telestion.adapter.mavlink.annotation.MavField;
import org.telestion.adapter.mavlink.annotation.MavInfo;
import org.telestion.adapter.mavlink.exception.AnnotationMissingException;
import org.telestion.adapter.mavlink.exception.InvalidChecksumException;
import org.telestion.adapter.mavlink.exception.ParsingException;
import org.telestion.adapter.mavlink.exception.WrongSignatureException;
import org.telestion.adapter.mavlink.message.MavlinkMessage;
import org.telestion.adapter.mavlink.message.MessageIndex;
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
public final class MavlinkParser extends AbstractVerticle {
	
	private final Logger logger = LoggerFactory.getLogger(MavlinkParser.class);
	
	public static final String toRawInAddress = Address.incoming(MavlinkParser.class, "toRaw");
	public static final String toRawOutAddress = Address.outgoing(MavlinkParser.class, "toRaw");
	
	public static final String toMavlinkInAddress = Address.incoming(MavlinkParser.class, "toMavlink");
	public static final String toMavlinkOutAddress = Address.incoming(MavlinkParser.class, "toMavlink");
	
	private Object parse(AtomicInteger index, byte[] payload, RecordComponent c) {
		long l = 0;
		switch(c.getAnnotation(MavField.class).nativeType().size) {
		case 1:
			l = Byte.toUnsignedInt(payload[index.incrementAndGet()]);
			break;
		case 2:
			l = Short.toUnsignedInt((short) (payload[index.incrementAndGet()] << 8 +
											 payload[index.incrementAndGet()]));
			break;
		case 4:
			l = Integer.toUnsignedLong(	payload[index.incrementAndGet()] << 24 +
										payload[index.incrementAndGet()] << 16 +
										payload[index.incrementAndGet()] << 8 +
										payload[index.incrementAndGet()]);
			break;
		case 8:
			l =		payload[index.incrementAndGet()] << 56 +
					payload[index.incrementAndGet()] << 48 +
					payload[index.incrementAndGet()] << 40 +
					payload[index.incrementAndGet()] << 32 +
					payload[index.incrementAndGet()] << 24 +
					payload[index.incrementAndGet()] << 16 +
					payload[index.incrementAndGet()] << 8 +
					payload[index.incrementAndGet()];
			break;
		default:
			throw new ParsingException("Parsing failed due to invalid Payload-Type!");
		}
		return (Number) c.getType().cast(l);
	}
	
	private Object[] parseArray(AtomicInteger index, byte[] payload, RecordComponent c, int length) {
		List<Object> nums = Collections.emptyList();
		for (int i = 0; i < length; i++) {
			nums.add(parse(index, payload, c));
		}
		return nums.toArray(Object[]::new);
	}
	
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(toMavlinkInAddress, msg -> {
			if (!(JsonMessage.on(RawMavlinkV2.class, msg, v2 -> {				
				Class<? extends MavlinkMessage> mavlinkClass = MessageIndex.get(v2.msgId());
				
				/*
				 * Check signature and checksum
				 */
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
					
					try {
						if (MavV2Signator.generateSignature(SecretKeySafe.getInstance().getSecretKey(),
								Arrays.copyOfRange(v2.getRaw(), 1, 11),
								Arrays.copyOfRange(v2.getRaw(), 12, 12 + v2.payload().payload().length),
								crcExtra, v2.linkId()) == v2.signature()) {
							// TODO: Log those -> @Matei: You need to add a send for logging bad packet!
							throw new WrongSignatureException("Signature of received MAVLink-Package was incorrect!");						}
					} catch (NoSuchAlgorithmException e) {
						// TODO: Log those -> @Matei: You need to add a send for logging bad packet!
						throw new WrongSignatureException("An unexpected error occured while checking the signature "
								+ "of the received MAVLink-Package ", e);
					}
				} else {
					throw new AnnotationMissingException("MavInfo-Annotation is missing!");
				}
				
				RecordComponent[] components = mavlinkClass.getRecordComponents();
				// There may be undefined behavior if position is not defined in all cases but in some
				if (Arrays.stream(components).filter(c -> c.isAnnotationPresent(MavField.class) &&
						c.getAnnotation(MavField.class).position() != -1).findFirst().isPresent()) {
					components = Arrays.stream(components)
							.filter(c -> c.isAnnotationPresent(MavField.class) &&
									c.getAnnotation(MavField.class).position() != -1)
							.sorted((c1, c2) -> 
								c1.getAnnotation(MavField.class).position() -
								c2.getAnnotation(MavField.class).position())
							.toArray(RecordComponent[]::new);
				}
				
				/*
				 * Start Reflection
				 */
				try {
					@SuppressWarnings("rawtypes")
					Class[] componentTypes = Arrays.stream(components)
							.map(c -> c.getType())
							.toArray(Class[]::new);
					
					final AtomicInteger index = new AtomicInteger(-1);
					final byte[] payload = v2.payload().payload();
					
					Constructor<? extends MavlinkMessage> constructor = mavlinkClass.getConstructor(componentTypes);
					Object[] parameters = Arrays.stream(components)
							.map(c -> {
								return c.isAnnotationPresent(MavArray.class)
										? parseArray(index, payload, c, c.getAnnotation(MavArray.class).length())
												: parse(index, payload, c);
							}).toArray(Object[]::new);
					
					MavlinkMessage m = constructor.newInstance(parameters);
					
					vertx.eventBus().publish(toMavlinkOutAddress, m.json());
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					throw new ParsingException(new ReflectionException(e));
				}
				
			}) || JsonMessage.on(RawMavlinkV1.class, msg, v1 -> {
				System.out.println("Ich lebe v1");
			}))) {
				if (!JsonMessage.on(RawMavlink.class, msg, unsupported -> {
					logger.warn("Unsupported RawMavlink-Package received on {}", msg.address());
				})) {
					logger.error("Unsupported type sent to {}", msg.address());
				}
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
