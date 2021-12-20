package de.wuespace.telestion.api.message.header;

import de.wuespace.telestion.api.utils.AbstractUtils;
import de.wuespace.telestion.api.utils.NoSuchPrimitiveTypeException;
import de.wuespace.telestion.api.utils.PrimitiveTypeParser;

import java.util.Objects;

/**
 * @author Ludwig Richter (@fussel178), Cedric Boes (@cb0s)
 */
public class InformationCodec extends AbstractUtils {
	public static String encode(Object value) {
		if (Objects.isNull(value)) return null;
		return String.valueOf(value);
	}

	public static <T> T decode(String encoded, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		return PrimitiveTypeParser.parse(encoded, primitiveClass);
	}

	public static <T> T decodeUnsigned(String encoded, Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		return PrimitiveTypeParser.parseUnsigned(encoded, primitiveClass);
	}

	public static <T> T getDefault(Class<T> primitiveClass) throws NoSuchPrimitiveTypeException {
		return PrimitiveTypeParser.getDefault(primitiveClass);
	}
}
