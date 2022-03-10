package de.wuespace.telestion.api.message;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <h2>Description</h2>
 *
 * <p>
 * The {@link HeaderInformation} class wraps APIs to attach information to messages
 * to transfer them over the {@link io.vertx.core.eventbus.EventBus Vert.x event bus}.
 * <p>
 * This implementation extends the already existing functionality of the {@link MultiMap Vert.x message headers}
 * to allow storing and retrieving of other basic data types than {@link String}.
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public class MyVerticle extends TelestionVerticle<GenericConfiguration> implements WithEventBus {
 *     @Override
 *     public void onStart() {
 *         var requestInfos = new HeaderInformation()
 *                 .add("version", 1)
 *                 .add("log-enabled", true)
 *                 .add("my-initial-char", 'C').
 *                 .add("my-coworkers-name", "fussel178");
 *
 *         request("request-address", "Hello with Info", requestInfos).onSuccess(message -> {
 *             var responseInfos = HeaderInformation.from(message);
 *
 *             logger.info("Received version: {}", responseInfos.getInt("version", -1));
 *             logger.info("Received log enabled state: {}", responseInfos.getBoolean("log-enabled", false));
 *             logger.info("Received initial char: {}", responseInfos.getChar("my-initial-char", '\0'));
 *             logger.info("Received coworker name: {}", responseInfos.get("my-coworkers-name", "unset"));
 *         });
 *     }
 * }
 * }
 * </pre>
 *
 * @author Cedric Boes (@cb0s), Ludwig Richter (@fussel178)
 * @see de.wuespace.telestion.api.verticle.trait.WithEventBus
 */
public class HeaderInformation {

	/**
	 * Merges an array of {@link HeaderInformation} objects into one {@link HeaderInformation} object.
	 *
	 * @param information multiple {@link HeaderInformation} objects you want to merge
	 * @return the merged {@link HeaderInformation} object
	 */
	public static HeaderInformation merge(HeaderInformation... information) {
		return merge(Arrays.stream(information));
	}

	/**
	 * Merges a list of {@link HeaderInformation} objects into one {@link HeaderInformation} object.
	 *
	 * @param list multiple {@link HeaderInformation} objects you want to merge
	 * @return the merged {@link HeaderInformation} object
	 */
	public static HeaderInformation merge(List<HeaderInformation> list) {
		return merge(list.stream());
	}

	/**
	 * Merges a stream of {@link HeaderInformation} objects into one {@link HeaderInformation} object.
	 *
	 * @param stream a stream that contains multiple {@link HeaderInformation} objects you want to merge
	 * @return the merged {@link HeaderInformation} object
	 */
	public static HeaderInformation merge(Stream<HeaderInformation> stream) {
		var headers = stream.map(HeaderInformation::getHeaders);
		return new HeaderInformation(MultiMapUtils.merge(headers));
	}

	/**
	 * Creates a new {@link HeaderInformation} object from existing {@link MultiMap Vert.x headers}.
	 *
	 * @param headers the {@link MultiMap Vert.x headers} you want to wrap into the {@link HeaderInformation} object
	 * @return a new {@link HeaderInformation} object with the wrapped {@link MultiMap Vert.x headers}
	 */
	public static HeaderInformation from(MultiMap headers) {
		return new HeaderInformation(headers);
	}

	/**
	 * Creates a new {@link HeaderInformation} object from a {@link Message Vert.x message}.
	 *
	 * @param message the {@link Message Vert.x message} that contains the {@link MultiMap Vert.x headers}
	 *                you want to wrap into the {@link HeaderInformation} object
	 * @return a new {@link HeaderInformation} object with the {@link MultiMap Vert.x headers}
	 * from the {@link Message Vert.x message}
	 */
	public static HeaderInformation from(Message<?> message) {
		return new HeaderInformation(message);
	}

	/**
	 * Creates a new {@link HeaderInformation} object from {@link DeliveryOptions}.
	 *
	 * @param options {@link DeliveryOptions} that contain the {@link MultiMap Vert.x headers}
	 *                you want to wrap into the {@link HeaderInformation} object
	 * @return a new {@link HeaderInformation} object with the {@link MultiMap Vert.x headers}
	 * from the {@link DeliveryOptions}
	 */
	public static HeaderInformation from(DeliveryOptions options) {
		return new HeaderInformation(options);
	}

	/**
	 * Creates a new {@link HeaderInformation} object with empty headers.
	 *
	 * @see MultiMap#caseInsensitiveMultiMap()
	 */
	public HeaderInformation() {
		this(MultiMap.caseInsensitiveMultiMap());
	}

	/**
	 * Creates a new {@link HeaderInformation} object with the provided {@link MultiMap Vert.x headers}.
	 *
	 * @param headers the {@link MultiMap Vert.x headers} that you want to wrap inside
	 *                the {@link HeaderInformation} object
	 */
	public HeaderInformation(MultiMap headers) {
		this.headers = headers;
	}

	/**
	 * Creates a new {@link HeaderInformation} object with the headers from the {@link Message Vert.x message}.
	 *
	 * @param message the {@link Message Vert.x message} that contains the {@link MultiMap Vert.x headers}
	 *                which you want to wrap inside the {@link HeaderInformation} object
	 */
	public HeaderInformation(Message<?> message) {
		this(message.headers());
	}

	/**
	 * Creates a new {@link HeaderInformation} object with the headers from the {@link DeliveryOptions}.
	 *
	 * @param options the {@link DeliveryOptions} that contain the {@link MultiMap Vert.x headers}
	 *                which you want to wrap inside the {@link HeaderInformation} object
	 */
	public HeaderInformation(DeliveryOptions options) {
		this(options.getHeaders());
	}

	/**
	 * Returns the wrapped {@link MultiMap Vert.x headers} ready to use in {@link DeliveryOptions}
	 * or the {@link de.wuespace.telestion.api.verticle.trait.WithEventBus WithEventBus} verticle trait.
	 *
	 * @return the wrapped {@link MultiMap Vert.x headers}
	 */
	public MultiMap getHeaders() {
		return headers;
	}

	/**
	 * Attaches the wrapped {@link MultiMap Vert.x headers} to the {@link DeliveryOptions}
	 * and return them again for further usage.
	 *
	 * @param options the {@link DeliveryOptions} that receive the wrapped {@link MultiMap Vert.x headers}
	 * @return the {@link DeliveryOptions} for further usage
	 */
	public DeliveryOptions attach(DeliveryOptions options) {
		return options.setHeaders(headers);
	}

	/**
	 * Creates new and empty {@link DeliveryOptions} and attaches the wrapped {@link MultiMap Vert.x headers}
	 * to them.
	 *
	 * @return the new {@link DeliveryOptions} with the attached {@link MultiMap Vert.x headers}
	 */
	public DeliveryOptions toOptions() {
		return attach(new DeliveryOptions());
	}

	///
	/// GET METHODS
	///

	/**
	 * Returns the first stored value assigned to the key as {@link String}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<String> get(String key) {
		return Optional.ofNullable(headers.get(key));
	}

	/**
	 * Returns the first stored value assigned to the key as {@link String}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public String get(String key, String defaultValue) {
		return get(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Byte}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Byte> getByte(String key) {
		return get(key).map(safeParse(Byte::parseByte));
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Byte}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public byte getByte(String key, byte defaultValue) {
		return getByte(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Integer}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Integer> getInt(String key) {
		return get(key).map(safeParse(Integer::parseInt));
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Integer}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public int getInt(String key, int defaultValue) {
		return getInt(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Long}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Long> getLong(String key) {
		return get(key).map(safeParse(Long::parseLong));
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Long}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public long getLong(String key, long defaultValue) {
		return getLong(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Float}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Float> getFloat(String key) {
		return get(key).map(safeParse(Float::parseFloat));
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Float}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public float getFloat(String key, float defaultValue) {
		return getFloat(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Double}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Double> getDouble(String key) {
		return get(key).map(safeParse(Double::parseDouble));
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Double}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public double getDouble(String key, double defaultValue) {
		return getDouble(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Character}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Character> getChar(String key) {
		return get(key).map(value -> value.length() == 1 ? value.charAt(0) : null);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Character}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public char getChar(String key, char defaultValue) {
		return getChar(key).orElse(defaultValue);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Boolean}.
	 * If no value is assigned to the key, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the first stored value wrapped inside an {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Boolean> getBoolean(String key) {
		return get(key).map(Boolean::parseBoolean);
	}

	/**
	 * Returns the first stored value assigned to the key as {@link Boolean}.
	 * If no value is assigned to the key, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which is returned if no value is assigned to the key
	 * @return the first stored value or the default value if no value is assigned to the key
	 * @see MultiMap#get(String)
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return getBoolean(key).orElse(defaultValue);
	}

	/**
	 * Returns a list of all stored values assigned to the key as a list of {@link String Strings}.
	 *
	 * @param key the key to which the values are assigned
	 * @return all stored values as a list of {@link String Strings}
	 * @see MultiMap#getAll(String)
	 */
	public List<String> getAll(String key) {
		return headers.getAll(key);
	}

	///
	/// ADD METHODS
	///

	/**
	 * Appends multiple {@link String} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link String} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, String... values) {
		return add(key, Arrays.stream(values));
	}

	/**
	 * Appends multiple {@link Character} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Character} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Character... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Appends multiple {@link Integer} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Integer} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Integer... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Appends multiple {@link Long} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Long} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Long... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Appends multiple {@link Short} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Short} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Short... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Appends multiple {@link Byte} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Byte} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Byte... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Appends multiple {@link Double} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Double} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Double... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Appends multiple {@link Boolean} values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Boolean} values that you want to append to the key
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Boolean... values) {
		return add(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Intermediate step to append a stream of strings to the wrapped {@link MultiMap Vert.x headers}.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values should be assigned
	 * @param stream stream of values to assign to the given key
	 * @return a reference to {@code this}, so the API can be used fluently
	 */
	private HeaderInformation add(String key, Stream<String> stream) {
		var list = stream.toList();

		if (contains(key)) {
			var existing = getAll(key);
			logger.debug("The header information object already contains values assigned to that key. " +
							"Appending new values to existing values. Key: {}, Before: {}, Now: {}",
					key, existing, Stream.of(existing, list).toList());
		}

		headers.add(key, list);
		return this;
	}

	/**
	 * Appends all values assigned to their keys from the {@link MultiMap} to already existing values.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param headers the {@link MultiMap} that contains the new values
	 *                which you want to append to the existing values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#addAll(MultiMap)
	 */
	public HeaderInformation addAll(MultiMap headers) {
		this.headers.addAll(headers);
		return this;
	}

	/**
	 * Appends all values assigned to their keys from the {@link Map} to already existing values.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param headers the {@link Map} that contains the new values
	 *                which you want to append to the existing values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#addAll(Map)
	 */
	public HeaderInformation addAll(Map<String, String> headers) {
		this.headers.addAll(headers);
		return this;
	}

	/**
	 * Appends all values assigned to their keys from the {@link HeaderInformation} object to already existing values.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param information the {@link HeaderInformation} object that contains the new values
	 *                    which you want to append to the existing values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#addAll(MultiMap)
	 */
	public HeaderInformation addAll(HeaderInformation information) {
		return addAll(information.headers);
	}

	///
	/// SET METHODS
	///

	/**
	 * Replaces multiple {@link String} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link String} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, String... values) {
		return set(key, Arrays.stream(values));
	}

	/**
	 * Replaces multiple {@link Character} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Character} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Character... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Integer} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Integer} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Integer... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Long} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Long} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Long... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Short} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Short} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Short... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Byte} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Byte} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Byte... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Double} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Double} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Double... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Float} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Float} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Float... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Replaces multiple {@link Boolean} values with the old allocation assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to which the values are assigned
	 * @param values the new {@link Boolean} values that you want to replace with the old allocation
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Boolean... values) {
		return set(key, Arrays.stream(values).map(String::valueOf));
	}

	/**
	 * Intermediate step to replace a stream of strings with the old allocation
	 * in the wrapped {@link MultiMap Vert.x headers}.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key    the key to the values which should be replaced
	 * @param stream stream of values which replace the current values assigned to the given key
	 * @return a reference to {@code this}, so the API can be used fluently
	 */
	private HeaderInformation set(String key, Stream<String> stream) {
		var list = stream.toList();

		if (contains(key)) {
			var existing = getAll(key);
			logger.debug("The header information object already contains values assigned to that key. " +
					"Overriding existing values with new values. Key: {}, Before: {}, Now: {}", key, existing, list);
		}

		headers.set(key, list);
		return this;
	}

	/**
	 * Replaces all values assigned to their keys with the content of the {@link MultiMap}.
	 * All remaining values are cleared.
	 * It is effectively an entire replacement of the entire {@link MultiMap} instance.<br>
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param headers the {@link MultiMap} that contains the new values assigned to their keys
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#setAll(MultiMap)
	 */
	public HeaderInformation setAll(MultiMap headers) {
		this.headers.setAll(headers);
		return this;
	}

	/**
	 * Replaces all values assigned to their keys with the content of the {@link Map}.
	 * All remaining values are cleared.
	 * It is effectively an entire replacement of the entire {@link MultiMap} instance.<br>
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param headers the {@link Map} that contains the new values assigned to their keys
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#setAll(Map)
	 */
	public HeaderInformation setAll(Map<String, String> headers) {
		this.headers.setAll(headers);
		return this;
	}

	/**
	 * Replaces all values assigned to their keys with the content of the {@link HeaderInformation} object.
	 * All other existing key slots are cleared.
	 * It is effectively an entire replacement of the entire {@link MultiMap} instance.<br>
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param information the {@link HeaderInformation} object that contains the new values assigned to their keys
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see HeaderInformation#setAll(MultiMap)
	 */
	public HeaderInformation setAll(HeaderInformation information) {
		return setAll(information.headers);
	}

	///
	/// OTHER METHODS FROM MULTIMAP
	///

	/**
	 * Returns {@code true}, if at least one value is assigned to the key.
	 * If no value is assigned, {@code false} is returned instead.
	 *
	 * @param key the key to which the value can be assigned
	 * @return {@code true}, if at least one value is assigned to the key
	 */
	public boolean contains(String key) {
		return headers.contains(key);
	}

	/**
	 * Removes all values assigned to the key.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @param key the key to which the values are assigned
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#remove(String)
	 */
	public HeaderInformation remove(String key) {
		headers.remove(key);
		return this;
	}

	/**
	 * Clears all values assigned to their keys.
	 * Returns a reference to {@code this} for fluent design.
	 *
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#clear()
	 */
	public HeaderInformation clear() {
		headers.clear();
		return this;
	}

	/**
	 * Returns the number of all keys which have values assigned to.
	 *
	 * @return the number of all keys which have values assigned to
	 * @see MultiMap#size()
	 */
	public int size() {
		return headers.size();
	}

	/**
	 * Returns an immutable set of all keys which have values assigned to.
	 *
	 * @return an immutable set of all keys which have values assigned to
	 * @see MultiMap#names()
	 */
	public Set<String> names() {
		return headers.names();
	}

	/**
	 * Returns {@code true}, if the wrapped {@link MultiMap Vert.x headers} have no entries.
	 *
	 * @return {@code true} if the wrapped {@link MultiMap Vert.x headers} have no entries
	 * @see MultiMap#isEmpty()
	 */
	public boolean isEmpty() {
		return headers.isEmpty();
	}

	@Override
	public String toString() {
		return "%s[headers=%s]".formatted(getClass().getName(), headers);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (this == o) return true;
		if (o.getClass() != getClass()) return false;
		var that = (HeaderInformation) o;
		return headers.equals(that.headers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(headers);
	}

	/**
	 * The wrapped {@link MultiMap Vert.x headers} instance that is manipulated
	 * through the exposed {@link HeaderInformation} APIs.
	 */
	private final MultiMap headers;

	private static final Logger logger = LoggerFactory.getLogger(HeaderInformation.class);

	/**
	 * Wraps the specified function into a {@link NumberFormatException} try-catch block
	 * to prevent exception breakout in the parsing steps.
	 * If the value cannot be parsed, {@code null} is returned instead.
	 *
	 * @param func the function that can throw a {@link NumberFormatException} during parsing
	 * @param <T>  the type of the input to the function
	 * @param <R>  the type of the result to the function
	 * @return another function which wraps the given function into a {@link NumberFormatException} try-catch block
	 */
	private static <T, R> Function<T, R> safeParse(Function<T, R> func) {
		return value -> {
			try {
				return func.apply(value);
			} catch (NumberFormatException e) {
				return null;
			}
		};
	}
}
