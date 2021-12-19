package de.wuespace.telestion.api.utils;

/**
 * Thrown to indicate that the primitive type parser has attempted to parse the raw value to its primitive type,
 * but that the given primitive class type is not known.
 *
 * If that exception occurs, please check if the primitive type parser knows
 * from the given primitive class type before continuing.
 *
 * @see PrimitiveTypeParser
 * @author Ludwig Richter
 */
public class NoSuchPrimitiveTypeException extends Exception {
	private final Class<?> clazz;

	/**
	 * Constructs a {@code NoSuchPrimitiveTypeException} with the unknown primitive class type
	 * and an additional cause.
	 * @param clazz the unknown primitive class type
	 * @param cause an additional cause
	 */
	public NoSuchPrimitiveTypeException(Class<?> clazz, Throwable cause) {
		super("Given primitive " + clazz.getName() +
				" cannot be used to parse a primitive value. Maybe it is not a primitive type at all?", cause);
		this.clazz = clazz;
	}

	/**
	 * Constructs a {@code NoSuchPrimitiveTypeException} with the unknown primitive class type.
	 * @param clazz the unknown primitive class type
	 */
	public NoSuchPrimitiveTypeException(Class<?> clazz) {
		this(clazz, null);
	}

	/**
	 * Returns the unknown primitive class type which causes this exception to be thrown
	 * in the {@link PrimitiveTypeParser}.
	 * @return the unknown primitive class type
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}
}
