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
 * @author Cedric Boes (@cb0s), Ludwig Richter (@fussel178)
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
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known or casting goes wrong
	 */
	public static <T> T parse(String raw, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!mappings.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		try {
			return primitiveClass.cast(mappings.get(primitiveClass).apply(raw));
		} catch (ClassCastException e) {
			throw new NoSuchPrimitiveTypeException(primitiveClass, e);
		}
	}

	/**
	 * Like {@link #parse(String, Class)} but parse numbers as unsigned.
	 * <p>
	 * <em>Note that the current implementation only checks if the transmitted data is an unsigned string.
	 * After parsing, it will then be cast to the given datatype.
	 * This might result in a negative value if the number is too big
	 * (e.g. {@code (int) (2 * Integer.MAX_VALUE)} would result in {@code -2} instead of double the value of an integer;
	 * this has to do with the underlying bits).</em>
	 *
	 * @param raw            the encoded/raw value as string
	 * @param primitiveClass the class of the primitive type to try to parse to
	 * @param <T>            class representation of the primitive type
	 * @return the parsed value in the primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known or casting goes wrong
	 */
	public static <T> T parseUnsigned(String raw, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		// TODO: We might need to improve support for unsigned values in the future at some point...
		if (!unsignedMappings.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		try {
			return primitiveClass.cast(unsignedMappings.get(primitiveClass).apply(raw));
		} catch (ClassCastException e) {
			throw new NoSuchPrimitiveTypeException(primitiveClass, e);
		}
	}

	/**
	 * Returns the default value for this primitive type and throws
	 * if the primitive class type is not known.
	 *
	 * @param primitiveClass the class of the primitive type
	 * @param <T>            class representation of the primitive type
	 * @return the default value for this primitive type
	 * @throws NoSuchPrimitiveTypeException when the given primitive type is not known or casting goes wrong
	 */
	public static <T> T getDefault(Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		if (!defaults.containsKey(primitiveClass)) throw new NoSuchPrimitiveTypeException(primitiveClass);
		try {
			return primitiveClass.cast(defaults.get(primitiveClass));
		} catch(ClassCastException e) {
			throw new NoSuchPrimitiveTypeException(primitiveClass, e);
		}
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
		mappings.put(byte.class, Byte::parseByte);
		mappings.put(Byte.class, Byte::parseByte);
		mappings.put(char.class, s -> s.length() == 1 ? s.charAt(0) : '\0');
		mappings.put(Character.class, s -> s.length() == 1 ? s.charAt(0) : '\0');
		mappings.put(short.class, Short::parseShort);
		mappings.put(Short.class, Short::parseShort);
		mappings.put(int.class, Integer::parseInt);
		mappings.put(Integer.class, Integer::parseInt);
		mappings.put(long.class, Long::parseLong);
		mappings.put(Long.class, Long::parseLong);
		mappings.put(float.class, Float::parseFloat);
		mappings.put(Float.class, Float::parseFloat);
		mappings.put(double.class, Double::parseDouble);
		mappings.put(Double.class, Double::parseDouble);
		mappings.put(String.class, s -> s);

		Function<Double, Double> unsignedFloatingCheck = x -> {
			if (x < 0.0) {
				throw new NumberFormatException(
						"When parsing to an unsigned type the number must not be smaller than 0! (given: %s)"
								.formatted(x));
			}
			return x;
		};

		// fill unsigned with same content and overwrite primitive types that can be unsigned
		unsignedMappings.putAll(mappings);
		unsignedMappings.put(byte.class, Short::parseShort);
		unsignedMappings.put(Byte.class, Short::parseShort);
		unsignedMappings.put(short.class, Integer::parseInt);
		unsignedMappings.put(Short.class, Integer::parseInt);
		unsignedMappings.put(int.class, Integer::parseUnsignedInt);
		unsignedMappings.put(Integer.class, Integer::parseUnsignedInt);
		unsignedMappings.put(long.class, Long::parseUnsignedLong);
		unsignedMappings.put(Long.class, Long::parseUnsignedLong);
		unsignedMappings.put(float.class, x -> unsignedFloatingCheck.apply(Double.parseDouble(x)));
		unsignedMappings.put(Float.class, x -> unsignedFloatingCheck.apply(Double.parseDouble(x)));
		unsignedMappings.put(double.class, x -> unsignedFloatingCheck.apply(Double.parseDouble(x)));
		unsignedMappings.put(Double.class, x -> unsignedFloatingCheck.apply(Double.parseDouble(x)));

		defaults.put(byte.class, (byte) 0);
		defaults.put(Byte.class, (byte) 0);
		defaults.put(char.class, '\0');
		defaults.put(Character.class, '\0');
		defaults.put(short.class, (short) 0);
		defaults.put(Short.class, (short) 0);
		defaults.put(int.class, 0);
		defaults.put(Integer.class, 0);
		defaults.put(long.class, 0L);
		defaults.put(Long.class, 0L);
		defaults.put(float.class, 0.0f);
		defaults.put(Float.class, 0.0f);
		defaults.put(double.class, 0.0);
		defaults.put(Double.class, 0.0);
		defaults.put(String.class, null);
	}
}
