package de.wuespace.telestion.api.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Thrown to indicate that the primitive type parser has attempted to parse the raw value to its primitive type,
 * but that the given primitive class type is not known.
 * <p>
 * If that exception occurs, please check if the primitive type parser knows
 * from the given primitive class type before continuing.
 *
 * @author Ludwig Richter (@fussel178)
 * @see PrimitiveTypeParser
 */
public class NoSuchPrimitiveTypeException extends RuntimeException {
	private final Class<?> clazz;

	/**
	 * Constructs a {@code NoSuchPrimitiveTypeException} with the unknown primitive class type
	 * and an additional cause.
	 *
	 * @param clazz the unknown primitive class type
	 * @param cause an additional cause
	 */
	public NoSuchPrimitiveTypeException(Class<?> clazz, Throwable cause) {
		super("Given primitive %s cannot be used to parse a primitive value. Expected: %s, Got: %s"
				.formatted(clazz.getName(), formattedSupportedPrimitiveTypes(), clazz.getName()), cause);
		this.clazz = clazz;
	}

	/**
	 * Constructs a {@code NoSuchPrimitiveTypeException} with the unknown primitive class type.
	 *
	 * @param clazz the unknown primitive class type
	 */
	public NoSuchPrimitiveTypeException(Class<?> clazz) {
		this(clazz, null);
	}

	/**
	 * Returns the unknown primitive class type which causes this exception to be thrown
	 * in the {@link PrimitiveTypeParser}.
	 *
	 * @return the unknown primitive class type
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}

	// [Integer, Long, ...]
	private static String formattedSupportedPrimitiveTypes() {
		return "[%s]".formatted(
				Arrays.stream(PrimitiveTypeParser.getSupportedPrimitiveTypes())
						.map(Class::getName)
						.collect(Collectors.joining(", "))
		);
	}
}
