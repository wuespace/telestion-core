package org.telestion.protocol.mavlink;

import java.util.HashMap;
import java.util.function.Function;

public class DefaultTypeParser {
	public static final HashMap<Class<?>, Function<Object, ?>> DEFAULT_PARSER;

	static {
		DEFAULT_PARSER = new HashMap<>();

		DEFAULT_PARSER.put(Byte.class, Function.identity());

		DEFAULT_PARSER.put(Short.class, Function.identity());

		DEFAULT_PARSER.put(Integer.class, Function.identity());

		DEFAULT_PARSER.put(Long.class, Function.identity());
	}
}
