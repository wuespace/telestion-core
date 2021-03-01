package org.telestion.protocol.mavlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.protocol.mavlink.annotation.MavArray;
import org.telestion.protocol.mavlink.annotation.MavField;
import org.telestion.protocol.mavlink.exception.AnnotationMissingException;
import org.telestion.protocol.mavlink.message.internal.ValidatedMavlinkPacket;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Cedric Boes
 * @version 1.0
 */
public class PayloadParser extends AbstractVerticle {
	public static final HashMap<Class<?>, TypeParser<?>> DEFAULT_TYPE_PARSER;

	static {
		DEFAULT_TYPE_PARSER = new HashMap<>();
		DEFAULT_TYPE_PARSER.put(byte.class, (payload, arraySize) ->  {
			if (arraySize == 0) {
				return payload[0];
			} else {
				return payload;
			}
		});

		DEFAULT_TYPE_PARSER.put(short.class, (payload, arraySize) -> {
			var data = new short[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (byte) (payload[2 * i + 1] << 8 + payload[2 * i]);
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		DEFAULT_TYPE_PARSER.put(int.class, (payload, arraySize) -> {
			var data = new int[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = payload[4*i + 3] << 24 + payload[4*i + 2] << 16 + payload[4*i + 1] << 8 + payload[4*i];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		DEFAULT_TYPE_PARSER.put(long.class, (payload, arraySize) -> {
			var data = new long[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (long) payload[8 * i + 7] << 56 + payload[8*i + 6] << 48 + payload[8*i + 5] << 40
						+ payload[8*i + 4] << 32 + payload[8*i + 3] << 24 + payload[8*i + 2] << 16
						+ payload[8*i + 1] << 8 + payload[8*i];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		DEFAULT_TYPE_PARSER.put(float.class, (payload, arraySize) -> {
			var data = new float[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = Float.intBitsToFloat(payload[4*i + 3] << 24 + payload[4*i + 2] << 16 + payload[4*i + 1] << 8
						+ payload[4*i]);
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		DEFAULT_TYPE_PARSER.put(double.class, (payload, arraySize) -> {
			var data = new double[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = Double.longBitsToDouble((long) payload[8 * i + 7] << 56 + payload[8*i + 6] << 48
						+ payload[8*i + 5] << 40 + payload[8*i + 4] << 32 + payload[8*i + 3] << 24
						+ payload[8*i + 2] << 16 + payload[8*i + 1] << 8 + payload[8*i]);
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		DEFAULT_TYPE_PARSER.put(char.class, (payload, arraySize) -> {
			var data = new char[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (char) payload[i];
			}

			if (arraySize == 0) {
				return data[0];
			} else {
				return String.valueOf(data);
			}
		});
	}

	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(config.inAddress(), msg -> {
			JsonMessage.on(ValidatedMavlinkPacket.class, msg, validatedMavlink -> {
				var clazz = validatedMavlink.clazz();
				var payload = validatedMavlink.payload();
				var components = clazz.getRecordComponents();

				try {
					components = Arrays.stream(components).sorted(PayloadParser::compareRecordComponents)
							.toArray(RecordComponent[]::new);
				} catch(AnnotationMissingException e) {
					logger.error("Parsing packet failed!" +
							"At least one Record-Component of Mavlink-message {} does not have the required " +
							"@MavField annotation!", clazz.getName());
					return;
				}

				try {
					Object[] objs = new Object[components.length];

					int currentIndex = 0;
					for (var c : components) {
						var annotation = c.getAnnotation(MavField.class);
						// Extensions are optional and at the end of the parsing process
						if (payload.length - 1 - currentIndex == 0 && annotation.extension()) {
							break;
						}

						var arrLength = c.isAnnotationPresent(MavArray.class) ?
								c.getAnnotation(MavArray.class).length() : 0;
						var type = c.getAnnotation(MavField.class).nativeType();

						currentIndex += type.size * (arrLength == 0 ? 1 : arrLength);
					}

					var constructor = clazz.getConstructor(Arrays.stream(components)
							.map(RecordComponent::getType).toArray(Class[]::new));

					vertx.eventBus().publish(getOutAddress(), constructor.newInstance(objs));
				} catch(Exception e) {
					logger.error("Parsing packet payload failed due to an unexpected error!", e);
				}
			});
		});
		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	/**
	 * @param inAddress
	 * @param outAddress
	 */
	public record Configuration(@JsonProperty String inAddress,
								@JsonProperty String outAddress) implements JsonMessage {
		/**
		 * Used for reflection!
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null);
		}
	}

	/**
	 *
	 * @param inAddress
	 * @param outAddress
	 */
	public PayloadParser(String inAddress, String outAddress) {
		this(new Configuration(inAddress, outAddress));
	}

	/**
	 *
	 * @param config
	 */
	public PayloadParser(Configuration config) {
		this.config = config;
		parser = new HashMap<>(DEFAULT_TYPE_PARSER);
	}

	/**
	 * Getter for {@link Configuration#inAddress inAddress}.
	 *
	 * @return {@link Configuration#inAddress inAddress}
	 */
	public String getInAddress() {
		return config.inAddress();
	}

	/**
	 * Getter for {@link Configuration#outAddress outAddress}.
	 *
	 * @return {@link Configuration#outAddress outAddress}
	 */
	public String getOutAddress() {
		return config.outAddress();
	}

	/**
	 * {@link Comparator} for the {@link RecordComponent MAVLink-RecordComponents} to bring them into the right format
	 * for MAVLink.
	 *
	 * @param c1 {@link RecordComponent} #1
	 * @param c2 {@link RecordComponent} #2
	 * @return how the sorting algorithm should sort
	 */
	private static int compareRecordComponents(RecordComponent c1, RecordComponent c2) {
		if (!(c1.isAnnotationPresent(MavField.class) && c2.isAnnotationPresent(MavField.class))) {
			// breaks out of method
			throw new AnnotationMissingException("@MavField-Annotation is missing for at least one RecordComponent!");
		}

		var mf1 = c1.getAnnotation(MavField.class);
		var mf2 = c2.getAnnotation(MavField.class);

		if (!(mf1.position() == -1 || mf2.position() == -1)) {
			return mf1.position() - mf2.position();
		}

		if (mf1.extension() == mf2.extension()) {
			return mf2.nativeType().size - mf1.nativeType().size;
		} else {
			return mf1.extension() ? 1 : -1;
		}
	}

	/**
	 *
	 */
	private final Logger logger = LoggerFactory.getLogger(PayloadParser.class);

	/**
	 * A configuration which specifies this in and out address for this verticle.<br>
	 * This can also be loaded from a config.
	 */
	private final Configuration config;

	private final HashMap<Class<?>, TypeParser> parser;
}
