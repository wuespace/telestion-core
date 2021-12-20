package de.wuespace.telestion.api.message.header;

import de.wuespace.telestion.api.utils.AbstractUtils;
import de.wuespace.telestion.api.utils.NoSuchPrimitiveTypeException;
import de.wuespace.telestion.api.utils.PrimitiveTypeParser;

import java.util.Objects;

/**
 * A utility class helping to encode and decode {@link Information} with the {@link PrimitiveTypeParser}.
 *
 * @author Ludwig Richter (@fussel178), Cedric Boes (@cb0s)
 */
public class InformationCodec extends AbstractUtils {
	/**
	 * Encodes a given object by calling {@link String#valueOf(Object)}. If the specified object is primitive,
	 * the corresponding variant of {@code String.valueOf()} is called.
	 *
	 * @param value to get encode as a {@link String}
	 * @return String representation of the given object
	 */
	public static String encode(Object value) {
		if (Objects.isNull(value)) return null;
		return String.valueOf(value);
	}

	/**
	 * Decodes a given encoded {@link String} and tries to map it to a primitive datatype (or String). It is a wrapper
	 * to the {@link PrimitiveTypeParser}.
	 *
	 * @param encoded			{@link String} to decode
	 * @param primitiveClass	type to map to
	 * @param <T>	type to decode to
	 * @return	decoded primitive type from the given {@link String}
	 * @throws NoSuchPrimitiveTypeException if decoding fails
	 */
	public static <T> T decode(String encoded, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		return PrimitiveTypeParser.parse(encoded, primitiveClass);
	}

	/**
	 * Like {@link #decode(String, Class)} but decoded result is unsigned.
	 *
	 * @param encoded        the encoded/raw value as string
	 * @param primitiveClass the class of the primitive type to try to parse to
	 * @param <T>            class representation of the primitive type
	 * @throws NoSuchPrimitiveTypeException if decoding fails
	 */
	public static <T> T decodeUnsigned(String encoded, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		return PrimitiveTypeParser.parseUnsigned(encoded, primitiveClass);
	}

	public static <T> T getDefault(Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		return PrimitiveTypeParser.getDefault(primitiveClass);
	}
}
