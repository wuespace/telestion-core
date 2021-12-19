package de.wuespace.telestion.api.header.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the record component is serializable in a Vert.x message header.
 * It gives the {@link de.wuespace.telestion.api.header.Information Information record}
 * additional information about its components
 * like name in the header space or a default value in case there are no information
 * from the message header available.
 *
 * @see SerializationUtils
 * @see de.wuespace.telestion.api.header.Information
 * @author Cedric Boes, Ludwig Richter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
public @interface SerializationInfo {
	/**
	 * The value of {@link #defaultValue()} if no default value for the record component is given.
	 */
	String NO_DEFAULT_VALUE = "";

	/**
	 * The name of header space where the record component should be stored and accessed.
	 * @return the name/key of the header space
	 */
	String name();

	/**
	 * The default value in case there are no information from the message header available.
	 * This property is optional. If no default value is defined,
	 * it falls back to {@link #NO_DEFAULT_VALUE}.
	 * @return the default value of the record component
	 */
	String defaultValue() default NO_DEFAULT_VALUE;

	/**
	 * Indicates, if the primitive type should pe parsed as unsigned integer when possible.
	 * Defaults to {@code false}.
	 *
	 * @see de.wuespace.telestion.api.header.InformationCodec#decodeUnsigned(String, Class)
	 * @return {@code true} if the primitive type should pe parsed as unsigned integer when possible
	 */
	boolean isUnsigned() default false;

	/**
	 * Indicates, that an attribute can be set to {@code null}.
	 * Defaults to {@code true}.
	 *
	 * @return {@code true} if an argument can take the {@code null}, {@code false} otherwise
	 */
	boolean isNullable() default true;
}
