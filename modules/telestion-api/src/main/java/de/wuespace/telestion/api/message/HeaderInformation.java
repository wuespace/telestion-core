package de.wuespace.telestion.api.message;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <h2>Description</h2>
 *
 * <p>
 * Header information are useful to attach simple information to messages
 * that should be transferred over the Vert.x event bus.
 * <p>
 * This implementation extends the already existing functionality
 * of the {@link Message Vert.x message} {@link MultiMap headers}
 * to allow storing and retrieving of other basic data types than {@link String}.
 * <p>
 * There are numerous interfaces that simplifies the usage with the existing features of Vert.x
 * and better support through the {@link de.wuespace.telestion.api.verticle.trait.WithEventBus WithEventBus trait}
 * for {@link io.vertx.core.Verticle Vert.x verticles}
 * and especially the {@link de.wuespace.telestion.api.verticle.TelestionVerticle TelestionVerticle}.
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
 */
public class HeaderInformation {

	/**
	 * Merges an array of header information into one header information object.
	 *
	 * @param information multiple header information that should be merged into one
	 * @return the merged header information
	 */
	public static HeaderInformation merge(HeaderInformation... information) {
		return merge(Arrays.stream(information));
	}

	/**
	 * Merges a list of header information into one header information object.
	 *
	 * @param list multiple header information that should be merged into one
	 * @return the merged header information
	 */
	public static HeaderInformation merge(List<HeaderInformation> list) {
		return merge(list.stream());
	}

	/**
	 * Merges a stream of header information into one header information object.
	 *
	 * @param stream a stream that contains multiple header information that should be merged into one
	 * @return the merged header information
	 */
	public static HeaderInformation merge(Stream<HeaderInformation> stream) {
		var headers = stream.map(HeaderInformation::getHeaders);
		return new HeaderInformation(MultiMapUtils.merge(headers));
	}

	/**
	 * Creates new header information from existing {@link MultiMap Vert.x headers}.
	 *
	 * @param headers the basic {@link MultiMap Vert.x headers} that should be wrapped into the header information
	 * @return new header information with the wrapped {@link MultiMap Vert.x headers}
	 */
	public static HeaderInformation from(MultiMap headers) {
		return new HeaderInformation(headers);
	}

	/**
	 * Creates new header information from existing {@link Message Vert.x message}.
	 *
	 * @param message the received {@link Message Vert.x message} which contains the received headers
	 * @return new header information with the received headers from the {@link Message Vert.x message}
	 */
	public static HeaderInformation from(Message<?> message) {
		return new HeaderInformation(message);
	}

	/**
	 * Creates new header information from existing {@link DeliveryOptions}.
	 *
	 * @param options existing {@link DeliveryOptions} which contain the basic {@link MultiMap Vert.x headers}
	 * @return new header information with the headers from the {@link DeliveryOptions}
	 */
	public static HeaderInformation from(DeliveryOptions options) {
		return new HeaderInformation(options);
	}

	/**
	 * Creates new header information with empty headers.
	 *
	 * @see MultiMap#caseInsensitiveMultiMap()
	 */
	public HeaderInformation() {
		this(MultiMap.caseInsensitiveMultiMap());
	}

	/**
	 * Creates new header information with the provided {@link MultiMap Vert.x headers}.
	 *
	 * @param headers the basic {@link MultiMap Vert.x headers} that should be wrapped inside the header information
	 */
	public HeaderInformation(MultiMap headers) {
		this.headers = headers;
	}

	/**
	 * Creates new header information with the headers from the given {@link Message Vert.x message}.
	 *
	 * @param message the {@link Message Vert.x message} that contains the basic {@link MultiMap Vert.x headers}
	 *                which should be wrapped inside the header information
	 */
	public HeaderInformation(Message<?> message) {
		this(message.headers());
	}

	/**
	 * Creates new header information with the headers from the given {@link DeliveryOptions}.
	 *
	 * @param options the {@link DeliveryOptions} that contain the basic {@link MultiMap Vert.x headers}
	 *                which should be wrapped inside the header information
	 */
	public HeaderInformation(DeliveryOptions options) {
		this(options.getHeaders());
	}

	/**
	 * Returns the wrapped basic {@link MultiMap Vert.x headers} ready to use in {@link DeliveryOptions}
	 * or the {@link de.wuespace.telestion.api.verticle.trait.WithEventBus WithEventBus} verticle trait.
	 *
	 * @return the wrapped basic {@link MultiMap Vert.x headers}
	 */
	public MultiMap getHeaders() {
		return headers;
	}

	/**
	 * Attaches the wrapped basic {@link MultiMap Vert.x headers} to the given {@link DeliveryOptions}
	 * and return them again for further usage.
	 *
	 * @param options the {@link DeliveryOptions} on which the wrapped basic {@link MultiMap Vert.x headers}
	 *                should be attached
	 * @return the given {@link DeliveryOptions} for further usage
	 */
	public DeliveryOptions attach(DeliveryOptions options) {
		return options.setHeaders(headers);
	}

	/**
	 * Creates new and empty {@link DeliveryOptions} and attaches the wrapped basic {@link MultiMap Vert.x headers}
	 * to them.
	 *
	 * @return the new {@link DeliveryOptions} with the attached basic {@link MultiMap Vert.x headers}
	 */
	public DeliveryOptions toOptions() {
		return attach(new DeliveryOptions());
	}

	///
	/// GET METHODS
	///

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers}.
	 * If no value in the key slot is found, an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<String> get(String key) {
		return Optional.ofNullable(headers.get(key));
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers}.
	 * If no value in the key slot is found, the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 * @return the value in the key slot or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public String get(String key, String defaultValue) {
		return get(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a byte.
	 * If no value in the key slot is found or the value cannot be converted to a byte,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as a byte wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Byte> getByte(String key) {
		return get(key).map(safeParse(Byte::parseByte));
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a byte.
	 * If no value in the key slot is found ir the value cannot be converted to a byte,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as a byte or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public byte getByte(String key, byte defaultValue) {
		return getByte(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as an integer.
	 * If no value in the key slot is found or the value cannot be converted to an integer,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as an integer wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Integer> getInt(String key) {
		return get(key).map(safeParse(Integer::parseInt));
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as an integer.
	 * If no value in the key slot is found ir the value cannot be converted to an integer,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as an integer or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public int getInt(String key, int defaultValue) {
		return getInt(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a long.
	 * If no value in the key slot is found or the value cannot be converted to a long,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as a long wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Long> getLong(String key) {
		return get(key).map(safeParse(Long::parseLong));
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a long.
	 * If no value in the key slot is found ir the value cannot be converted to a long,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as a long or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public long getLong(String key, long defaultValue) {
		return getLong(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a float.
	 * If no value in the key slot is found or the value cannot be converted to a float,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as a float wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Float> getFloat(String key) {
		return get(key).map(safeParse(Float::parseFloat));
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a float.
	 * If no value in the key slot is found ir the value cannot be converted to a float,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as a float or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public float getFloat(String key, float defaultValue) {
		return getFloat(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a double.
	 * If no value in the key slot is found or the value cannot be converted to a double,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as a double wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Double> getDouble(String key) {
		return get(key).map(safeParse(Double::parseDouble));
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a double.
	 * If no value in the key slot is found ir the value cannot be converted to a double,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as a double or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public double getDouble(String key, double defaultValue) {
		return getDouble(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a character.
	 * If no value in the key slot is found or the value cannot be converted to a character,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as a character wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Character> getChar(String key) {
		return get(key).map(value -> value.length() == 1 ? value.charAt(0) : null);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a character.
	 * If no value in the key slot is found ir the value cannot be converted to a character,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as a character or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public char getChar(String key, char defaultValue) {
		return getChar(key).orElse(defaultValue);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a boolean.
	 * If no value in the key slot is found or the value cannot be converted to a boolean,
	 * an {@link Optional#empty() empty Optional} is returned instead.
	 *
	 * @param key the key to which the value is assigned
	 * @return the value as a boolean wrapped inside a {@link Optional} for better {@code null} type safety
	 * @see MultiMap#get(String)
	 */
	public Optional<Boolean> getBoolean(String key) {
		return get(key).map(Boolean::parseBoolean);
	}

	/**
	 * Returns the current value of the given key in the {@link MultiMap Vert.x headers} as a character.
	 * If no value in the key slot is found ir the value cannot be converted to a character,
	 * the default value is used instead.
	 *
	 * @param key          the key to which the value is assigned
	 * @param defaultValue the value which are returned if no value in the key slot is found
	 *                     or the value in the key slot is not parsable
	 * @return the value in the key slot as a character or the default value if the key slot is empty
	 * @see MultiMap#get(String)
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return getBoolean(key).orElse(defaultValue);
	}

	/**
	 * Returns a list of all stored values in the specified key slot of the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @param key the key to which the values are assigned
	 * @return the stored values as a list or an empty list if no values are stored
	 * @see MultiMap#getAll(String)
	 */
	public List<String> getAll(String key) {
		return headers.getAll(key);
	}

	///
	/// ADD METHODS
	///

	/**
	 * Adds multiple strings to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new strings that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, String... values) {
		return add(key, List.of(values));
	}

	/**
	 * Adds multiple characters to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new characters that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Character... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Adds multiple integers to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new integers that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Integer... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Adds multiple longs to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new longs that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Long... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Adds multiple shorts to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new shorts that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Short... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Adds multiple bytes to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new bytes that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Byte... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Adds multiple doubles to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new doubles that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Double... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Adds multiple booleans to the specified key slot of the {@link MultiMap Vert.x headers}.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new booleans that should be added to the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#add(String, Iterable)
	 */
	public HeaderInformation add(String key, Boolean... values) {
		return add(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Intermediate step to add a list of strings to the wrapped {@link MultiMap Vert.x headers}.
	 */
	private HeaderInformation add(String key, List<String> list) {
		headers.add(key, list);
		return this;
	}

	///
	/// SET METHODS
	///

	/**
	 * Sets multiple strings in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new strings that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, String... values) {
		return set(key, List.of(values));
	}

	/**
	 * Sets multiple characters in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new characters that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Character... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple integers in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new integers that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Integer... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple longs in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new longs that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Long... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple shorts in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new shorts that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Short... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple bytes in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new bytes that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Byte... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple doubles in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new doubles that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Double... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple floats in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new floats that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Float... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Sets multiple booleans in the specified key slot of the {@link MultiMap Vert.x headers}
	 * and replaces potentially existing values.
	 *
	 * @param key    the key to which the value is assigned
	 * @param values the new booleans that should be replaced in the key slot
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#set(String, Iterable)
	 */
	public HeaderInformation set(String key, Boolean... values) {
		return set(key, Stream.of(values).map(String::valueOf).toList());
	}

	/**
	 * Intermediate step to set a list of strings in the wrapped {@link MultiMap Vert.x headers}.
	 */
	private HeaderInformation set(String key, List<String> values) {
		headers.set(key, values);
		return this;
	}

	///
	/// OTHER METHODS FROM MULTIMAP
	///

	/**
	 * Replaces all key slots of the wrapped {@link MultiMap Vert.x headers} with the specified ones.
	 * All other existing key slots are cleared.
	 * It is effectively an entire replacement of the entire {@link MultiMap} instance.
	 *
	 * @param headers the {@link MultiMap Vert.x headers} that contains the new key slots and associated values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#setAll(MultiMap)
	 */
	public HeaderInformation setAll(MultiMap headers) {
		this.headers.setAll(headers);
		return this;
	}

	/**
	 * Replaces all key slots of the wrapped {@link MultiMap Vert.x headers} with the specified ones.
	 * All other existing key slots are cleared.
	 * It is effectively an entire replacement of the entire {@link MultiMap} instance.
	 *
	 * @param headers a {@link Map} that contains the new key slots and associated values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#setAll(Map)
	 */
	public HeaderInformation setAll(Map<String, String> headers) {
		this.headers.setAll(headers);
		return this;
	}

	/**
	 * Replaces all key slots of the wrapped {@link MultiMap Vert.x headers}
	 * with the wrapped {@link MultiMap Vert.x headers} of the specified instance.
	 * All other existing key slots are cleared.
	 * It is effectively an entire replacement of the entire {@link MultiMap} instance.
	 *
	 * @param information the other {@link HeaderInformation} instance that contains the new key slots
	 *                    and associated values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see HeaderInformation#setAll(MultiMap)
	 */
	public HeaderInformation setAll(HeaderInformation information) {
		return setAll(information.headers);
	}

	/**
	 * Adds all key slots and values of the specified {@link MultiMap Vert.x headers}
	 * to the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @param headers the {@link MultiMap Vert.x headers} that contain the new key slots and values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#addAll(MultiMap)
	 */
	public HeaderInformation addAll(MultiMap headers) {
		this.headers.addAll(headers);
		return this;
	}

	/**
	 * Adds all key slots and values of the specified {@link Map} to the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @param headers the {@link Map} that contain the new key slots and values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#addAll(Map)
	 */
	public HeaderInformation addAll(Map<String, String> headers) {
		this.headers.addAll(headers);
		return this;
	}

	/**
	 * Adds all key slots and values of the specified {@link HeaderInformation} instance
	 * to the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @param information the other {@link HeaderInformation} instance that contains the new key slots
	 *                    and associated values
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#addAll(MultiMap)
	 */
	public HeaderInformation addAll(HeaderInformation information) {
		return addAll(information.headers);
	}

	/**
	 * Removes all values from the specified key slot on the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @param name the key to which the values are assigned
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#remove(String)
	 */
	public HeaderInformation remove(String name) {
		headers.remove(name);
		return this;
	}

	/**
	 * Clears all key slots and values from the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @return a reference to {@code this}, so the API can be used fluently
	 * @see MultiMap#clear()
	 */
	public HeaderInformation clear() {
		headers.clear();
		return this;
	}

	/**
	 * Returns the number of currently used key slots in the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @return the number of used key slots in the {@link MultiMap Vert.x headers}
	 * @see MultiMap#size()
	 */
	public int size() {
		return headers.size();
	}

	/**
	 * Returns an immutable set of all keys currently used in the wrapped {@link MultiMap Vert.x headers}.
	 *
	 * @return an immutable set of all keys in the wrapped {@link MultiMap Vert.x headers}
	 * @see MultiMap#names()
	 */
	public Set<String> names() {
		return headers.names();
	}

	/**
	 * Returns {@code true}, if the wrapped {@link MultiMap Vert.x headers} has no entries.
	 *
	 * @return returns {@code true} if the wrapped {@link MultiMap Vert.x headers} has no entries
	 * @see MultiMap#isEmpty()
	 */
	public boolean isEmpty() {
		return headers.isEmpty();
	}

	/**
	 * The wrapped {@link MultiMap Vert.x headers} instance that is manipulated
	 * through the exposed {@link HeaderInformation} APIs.
	 */
	private final MultiMap headers;

	/**
	 * Wraps the specified function into a {@link NumberFormatException} try-catch block
	 * to prevent exception breakout in the parsing steps above.
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
