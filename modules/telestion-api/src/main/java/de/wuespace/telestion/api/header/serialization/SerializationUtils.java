package de.wuespace.telestion.api.header.serialization;

import de.wuespace.telestion.api.utils.AbstractUtils;

import java.lang.reflect.RecordComponent;

/**
 * <p><strong>Utilities for {@link SerializationInfo}</strong></p>
 *
 * <p>
 *     It supplements {@link SerializationInfo} and adds further functionality.
 * </p>
 *
 * @see SerializationInfo
 * @see de.wuespace.telestion.api.header.Information
 * @author Ludwig Richter
 */
public class SerializationUtils extends AbstractUtils {
	/**
	 * Returns the serialization information for the record component.
	 * @param component the record component that should have serialization information
	 * @return the serialization information of the record component
	 * @throws NoSerializationException when the record component has no serialization information
	 */
	public static SerializationInfo serialize(RecordComponent component) throws NoSerializationException {
		if (!component.isAnnotationPresent(SerializationInfo.class)) {
			throw new NoSerializationException(component);
		}

		return component.getAnnotation(SerializationInfo.class);
	}

	/**
	 * Checks if the serialization information has no default value.
	 * @param serialization the serialization information with or without a default value
	 * @return {@code true} if the serialization information has a default value
	 */
	public static boolean hasNoDefault(SerializationInfo serialization) {
		return serialization.defaultValue().equals(SerializationInfo.NO_DEFAULT_VALUE);
	}

	/**
	 * Like {@link #hasNoDefault(SerializationInfo)},
	 * but extracts the serialization information from the record component.
	 * @param component the record component that should have serialization information with or without a default value
	 * @return {@code true} if the serialization information has a default value
	 * @throws NoSerializationException when the record component has no serialization information
	 */
	public static boolean hasNoDefault(RecordComponent component) throws NoSerializationException {
		return hasNoDefault(serialize(component));
	}

	/**
	 * Returns the default value of the serialization information
	 * or {@code null} when the default value is not available.
	 * @param serialization the serialization information with or without a default value
	 * @return the default value of the serialization information or {@code null} when not available
	 */
	public static String nullableDefaultValue(SerializationInfo serialization) {
		return hasNoDefault(serialization) ? null : serialization.defaultValue();
	}

	/**
	 * Returns the default value of the serialization information from the record component.
	 * If the default value is not available, it returns {@code null} instead.
	 * @param component the record component that should have serialization information with or without a default value
	 * @return the default value of the serialization information or {@code null} when not available
	 * @throws NoSerializationException when the record component has no serialization information
	 */
	public static String nullableDefaultValue(RecordComponent component) throws NoSerializationException {
		return nullableDefaultValue(serialize(component));
	}
}
