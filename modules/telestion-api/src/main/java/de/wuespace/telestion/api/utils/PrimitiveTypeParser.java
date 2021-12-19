package de.wuespace.telestion.api.utils;

import java.util.HashMap;
import java.util.function.Function;

/**
 * <p><strong>A utility class to parse values from strings for primitive types</strong></p>
 *
 * <p>
 * This utility class offers methods to parse values from a string input to primitive types via their class type.
 * </p>
 *
 * @author Cedric Boes, Ludwig Richter
 * @see #parse(String, Class)
 * @see #parseUnsigned(String, Class)
 */
public class PrimitiveTypeParser extends AbstractUtils {
	/**
	 * Tries to parse a value from the given string to the primitive type and throws
	 * if the primitive class type is not known.
	 *
	 * @param raw            the encoded/raw value as string
	 * @param primitiveClass the class of the primitive type to try to parse to
	 * @param <T>            class representation of the primitive type
	 * @return the parsed value in the primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parse(String raw, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!mappings.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		return (T) mappings.get(primitiveClass).apply(raw);
	}

	/**
	 * Like {@link #parse(String, Class)} but parse integers as unsigned.
	 *
	 * @param raw            the encoded/raw value as string
	 * @param primitiveClass the class of the primitive type to try to parse to
	 * @param <T>            class representation of the primitive type
	 * @return the parsed value in the primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parseUnsigned(String raw, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!unsignedMappings.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		return (T) unsignedMappings.get(primitiveClass).apply(raw);
	}

	/**
	 * Returns the default value for this primitive type and throws
	 * if the primitive class type is not known.
	 *
	 * @param primitiveClass the class of the primitive type
	 * @param <T>            class representation of the primitive type
	 * @return the default value for this primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDefault(Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!defaults.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		return (T) defaults.get(primitiveClass);
	}

	/**
	 * Returns all supported primitive types.
	 *
	 * @return all supported primitive types as class type list
	 */
	public static Class<?>[] getSupportedPrimitiveTypes() {
		return mappings.keySet().toArray(Class<?>[]::new);
	}

	private static final HashMap<Class<?>, Function<String, Object>> mappings = new HashMap<>();
	private static final HashMap<Class<?>, Function<String, Object>> unsignedMappings = new HashMap<>();
	private static final HashMap<Class<?>, Object> defaults = new HashMap<>();

	static {
		mappings.put(Byte.class, Byte::parseByte);
		mappings.put(Character.class, s -> s.length() == 1 ? s.charAt(0) : '\0');
		mappings.put(Short.class, Short::parseShort);
		mappings.put(Integer.class, Integer::parseInt);
		mappings.put(Long.class, Long::parseLong);
		mappings.put(Float.class, Float::parseFloat);
		mappings.put(Double.class, Double::parseDouble);
		mappings.put(String.class, s -> s);

		// fill unsigned with same content and overwrite primitive types that can be unsigned
		unsignedMappings.putAll(mappings);
		unsignedMappings.put(Integer.class, Integer::parseUnsignedInt);
		unsignedMappings.put(Long.class, Long::parseUnsignedLong);

		defaults.put(Byte.class, (byte) 0);
		defaults.put(Character.class, '\0');
		defaults.put(Short.class, (short) 0);
		defaults.put(Integer.class, 0);
		defaults.put(Long.class, 0L);
		defaults.put(Float.class, 0.0f);
		defaults.put(Double.class, 0.0);
		defaults.put(String.class, null);
	}
}
