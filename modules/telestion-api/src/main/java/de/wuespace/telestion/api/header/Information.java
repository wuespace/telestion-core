package de.wuespace.telestion.api.header;

import de.wuespace.telestion.api.header.serialization.NoSerializationException;
import de.wuespace.telestion.api.header.serialization.SerializationUtils;
import de.wuespace.telestion.api.utils.NoSuchPrimitiveTypeException;
import io.vertx.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.*;

/**
 * The base class for all information records for header metadata information.
 *
 * @author Cedric Boes (@cb0s), Ludwig Richter (@fussel178), Pablo Klaschka (@pklaschka)
 */
public interface Information {
	/**
	 * Appends all record information to the Vert.x message headers based on the serialization information
	 * on each record component.
	 * <p>
	 * If a record value is {@code null}, the default value from serialization information is used instead.
	 * <p>
	 * If a record component has no serialization information it is skipped on packing and a warning gets logged.
	 *
	 * @param headers existing Vert.x message headers to place the packed record information on
	 * @return the given headers with the packed record information
	 */
	default MultiMap appendToHeaders(MultiMap headers) {
		if (!this.getClass().isRecord()) {
			logger.error("%s is not a record. Please use a record as information type to use header utility functions."
					.formatted(this.getClass().getName()));
			return headers;
		}

		for (var component : this.getClass().getRecordComponents()) {
			try {
				var serialization = SerializationUtils.serialize(component);
				var value = getOrDefault(component);
				headers.set(serialization.name(), InformationCodec.encode(value));
			} catch (NoSerializationException e) {
				logger.warn(e.getMessage(), e);
			} catch (IllegalAccessException | InvocationTargetException e) {
				logger.error("An unexpected exception occurred. Cannot access record value {} in {}. " +
								"Please file a bug report at https://github.com/wuespace/telestion-core/issues/new",
						component.getName(), this.getClass().getName(), e);
			}
		}

		return headers;
	}

	/**
	 * Like {@link #appendToHeaders(MultiMap)} but uses new and empty headers instead.
	 *
	 * @return new headers only with the packed record information
	 */
	default MultiMap toHeaders() {
		return appendToHeaders(MultiMap.caseInsensitiveMultiMap());
	}

	/**
	 * Extracts record information from Vert.x message headers based on the serialization information
	 * on each record component.
	 * <p>
	 * If a record value is {@code null}, the default value from serialization information is used instead.
	 *
	 * @param headers          Vert.x message headers containing the required information
	 *                         to construct an information record
	 * @param informationClass the type of the information record
	 * @return the constructed information record or {@code null} if no value
	 * for a non-nullable record component is found
	 */
	static <T> T fromHeaders(MultiMap headers, Class<T> informationClass) {
		if (!informationClass.isRecord()) {
			logger.error("%s is not a record. Please use a record as information type to use header utility functions."
					.formatted(informationClass.getName()));
			return null;
		}

		var values = new ArrayList<>();

		for (var component : informationClass.getRecordComponents()) {
			try {
				var serialization = SerializationUtils.serialize(component);
				var defaultValue = SerializationUtils.nullableDefaultValue(serialization);
				var value = headers.get(serialization.name());
				value = Objects.isNull(value) ? defaultValue : value;

				if (Objects.isNull(value)) {
					if (!serialization.isNullable()) {
						logger.error("No value for record component {} found, but is explicitly required.",
								component.getName());
						return null;
					}
					values.add(InformationCodec.getDefault(component.getType()));
					continue;
				}

				values.add(serialization.isUnsigned()
						? InformationCodec.decodeUnsigned(value, component.getType())
						: InformationCodec.decode(value, component.getType()));
			} catch (NoSerializationException | NoSuchPrimitiveTypeException e) {
				logger.warn(e.getMessage(), e);
				values.add(null);
			}
		}

		return constructFromAttributes(values.toArray(), informationClass);
	}

	/**
	 * Returns the value of a record component or if {@code null} the default value
	 * from the serialization information is used instead.
	 *
	 * @param component the record component with a value and serialization information
	 * @return the value or the default value of the record component
	 * @throws InvocationTargetException when the value of the record component cannot be accessed
	 * @throws IllegalAccessException    when the value of the record component cannot be accessed
	 * @throws NoSerializationException  when the record component has no serialization information
	 */
	default Object getOrDefault(RecordComponent component)
			throws InvocationTargetException, IllegalAccessException, NoSerializationException {
		var defaultValue = SerializationUtils.nullableDefaultValue(component);
		var value = component.getAccessor().invoke(this);
		return Objects.isNull(value) ? defaultValue : value;
	}

	/**
	 * Constructs a new information record from a type and value map. The information type is given.
	 * <p>
	 * If not all type and value entries available to successfully construct the given information type,
	 * it returns {@code null} and logs an error message to the backend.
	 *
	 * @param values           the values of the information record in same order as record components
	 * @param informationClass the information type to should be generated
	 * @return the generated information record
	 */
	static <T> T constructFromAttributes(Object[] values,
										 Class<T> informationClass) {

		var classes = Arrays.stream(informationClass.getRecordComponents())
				.map(RecordComponent::getType)
				.toArray(Class<?>[]::new);

		try {
			return informationClass.getConstructor(classes).newInstance(values);
		} catch (InstantiationException
				| IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException
				| NoSuchMethodException e) {
			logger.error("An unexpected exception occurred. " +
							"Cannot construct {} from parsed header information (Classes: {}, Values: {}). " +
							"Please file a bug report at https://github.com/wuespace/telestion-core/issues/new",
					informationClass.getName(), Arrays.toString(classes), Arrays.toString(values), e);
			return null;
		}
	}

	Logger logger = LoggerFactory.getLogger(Information.class);
}
