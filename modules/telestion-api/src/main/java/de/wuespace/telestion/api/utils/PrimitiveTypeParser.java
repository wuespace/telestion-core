package de.wuespace.telestion.api.utils;

import java.util.HashMap;
import java.util.function.Function;

/**
 * <p><strong>A utility class to parse values from strings for primitive types</strong></p>
 *
 * <p>
 *     This utility class offers methods to parse values from a string input to primitive types via their class type.
 * </p>
 *
 * @see #parse(String, Class)
 * @see #parseUnsigned(String, Class)
 * @author Cedric Boes, Ludwig Richter
 */
public class PrimitiveTypeParser {
	/**
	 * Tries to parse a value from the given string to the primitive type and throws
	 * if the primitive class type is not known.
	 * @param raw the encoded/raw value as string
	 * @param primitiveClass the class of the primitive type to try to parse to
	 * @return the parsed value in the primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known
	 */
	public static Object parse(String raw, Class<?> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!mappings.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		return mappings.get(primitiveClass).apply(raw);
	}

	/**
	 * Like {@link #parse(String, Class)} but parse integers as unsigned.
	 * @param raw the encoded/raw value as string
	 * @param primitiveClass the class of the primitive type to try to parse to
	 * @return the parsed value in the primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known
	 */
	public static Object parseUnsigned(String raw, Class<?> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!unsignedMappings.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		return unsignedMappings.get(primitiveClass).apply(raw);
	}

	// utils classes cannot be instantiated
	private PrimitiveTypeParser() {}

	private static final HashMap<Class<?>, Function<String, Object>> mappings = new HashMap<>();
	private static final HashMap<Class<?>, Function<String, Object>> unsignedMappings = new HashMap<>();

	static {
		mappings.put(Byte.class, Byte::parseByte);
		mappings.put(Character.class, s -> s.length() == 1 ? s.charAt(0) : null);
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
	}
}
