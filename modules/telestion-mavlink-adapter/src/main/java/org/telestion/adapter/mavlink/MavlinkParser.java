package org.telestion.adapter.mavlink;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.annotation.MavArray;
import org.telestion.adapter.mavlink.annotation.MavField;
import org.telestion.adapter.mavlink.annotation.MavInfo;
import org.telestion.adapter.mavlink.exception.AnnotationMissingException;
import org.telestion.adapter.mavlink.exception.InvalidChecksumException;
import org.telestion.adapter.mavlink.exception.PacketException;
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
	
	public static final String toRawInAddressV1 = Address.incoming(MavlinkParser.class, "toRaw1");
	public static final String toRawInAddressV2 = Address.incoming(MavlinkParser.class, "toRaw2");
	public static final String toRawOutAddress = Address.outgoing(MavlinkParser.class, "toRaw");
	
	public static final String toMavlinkInAddress = Address.incoming(MavlinkParser.class, "toMavlink");
	public static final String toMavlinkOutAddress = Address.incoming(MavlinkParser.class, "toMavlink");
	
	private Object parse(AtomicInteger index, byte[] payload, RecordComponent c) {
		long l = 0;
		boolean unsigned = c.getAnnotation(MavField.class).nativeType().unsigned;
		switch(c.getAnnotation(MavField.class).nativeType().size) {
		case 1:
			short s = payload[index.incrementAndGet()];
			l = unsigned ? Short.toUnsignedInt(s) : s;
			break;
		case 2:
			int i = payload[index.incrementAndGet()] << 8 +
					payload[index.incrementAndGet()];
			l = unsigned ? Integer.toUnsignedLong(i) : i;
			break;
		case 4:
			i =	payload[index.incrementAndGet()] << 24 +
				payload[index.incrementAndGet()] << 16 +
				payload[index.incrementAndGet()] << 8 +
				payload[index.incrementAndGet()];
			l = unsigned ? Integer.toUnsignedLong(i) : i;
			break;
		case 8:
			// There is no real support for unsigned longs, yet!
			l =	payload[index.incrementAndGet()] << 56 +
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
		return c.getType().cast(l);
	}
	
	@SuppressWarnings("preview")
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(toMavlinkInAddress, msg -> {
			if (!(JsonMessage.on(RawMavlink.class, msg, raw -> {
				Class<? extends MavlinkMessage> mavlinkClass = null;
				
				int subtract = -1;
				int checksum = 0;
				byte[] load = null;
				
				if (raw instanceof RawMavlinkV2 v2) {
					mavlinkClass = MessageIndex.get(v2.msgId());
					
					if (v2.incompatFlags() == 0x1) {
						subtract = 12;
					}
					
					checksum = v2.checksum();
					load = v2.payload().payload();
				} else if (raw instanceof RawMavlinkV1 v1) {
					mavlinkClass = MessageIndex.get(v1.msgId());
					
					checksum = v1.checksum();
					load = v1.payload().payload();
				} else {
					logger.error("Unsupported MAVLink-Message received on {}!", msg.address());
					throw new PacketException("Unsupported MAVLink-Message received!");
				}
				
				/*
				 * Check signature and checksum
				 */
				if (mavlinkClass.isAnnotationPresent(MavInfo.class)) {
					MavInfo annotation = mavlinkClass.getAnnotation(MavInfo.class);
					int crcExtra = annotation.crc();
					byte[] buildArray = Arrays.copyOfRange(raw.getRaw(), 1, raw.getRaw().length - subtract);
					buildArray[buildArray.length-1] = (byte) crcExtra;

					int crc = X25Checksum.calculate(buildArray);
					
					if (crc != checksum) {
						// TODO: Log those -> @Matei: You need to add a send for logging bad packet!
						throw new InvalidChecksumException("Checksum of received MAVLink-Package was invalid!");
					}
					
					if (raw instanceof RawMavlinkV2 v2) {
						try {
							if (MavV2Signator.generateSignature(SecretKeySafe.getInstance().getSecretKey(),
									Arrays.copyOfRange(v2.getRaw(), 1, 11),
									Arrays.copyOfRange(v2.getRaw(), 12, 12 + v2.payload().payload().length),
									crcExtra, v2.linkId()) == v2.signature()) {
								// TODO: Log those -> @Matei: You need to add a send for logging bad packet!
								throw new WrongSignatureException("Signature of received MAVLink-Package was"
										+ "incorrect!");						}
						} catch (NoSuchAlgorithmException e) {
							// TODO: Log those -> @Matei: You need to add a send for logging bad packet!
							throw new WrongSignatureException("An unexpected error occured while checking the signature "
									+ "of the received MAVLink-Package ", e);
						}
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
					final byte[] payload = load;
					
					Constructor<? extends MavlinkMessage> constructor = mavlinkClass.getConstructor(componentTypes);
					Object[] parameters = Arrays.stream(components)
							.map(c -> {
								return c.isAnnotationPresent(MavArray.class)
										? IntStream.range(0, c.getAnnotation(MavArray.class).length())
												.mapToObj(i -> parse(index, payload, c))
												.toArray(Object[]::new)
										: parse(index, payload, c);
							}).toArray(Object[]::new);
					
					MavlinkMessage m = constructor.newInstance(parameters);
					
					vertx.eventBus().publish(toMavlinkOutAddress, m.json());
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					throw new ParsingException(new ReflectionException(e));
				}
				
			}))) {
				logger.error("Unsupported type sent to {}", msg.address());
				throw new PacketException("Unsupported type sent to Mavlink-Parser!");
			}
		});
		
		// TODO: Sending part!
		vertx.eventBus().consumer(toRawInAddressV1, msg -> {
			if (!JsonMessage.on(MavlinkMessage.class, msg, raw -> {

			})) {
				
			}
		});
		
		vertx.eventBus().consumer(toRawInAddressV2, msg -> {
			if (!JsonMessage.on(MavlinkMessage.class, msg, raw -> {
				
			})) {
				
			}
		});
		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
