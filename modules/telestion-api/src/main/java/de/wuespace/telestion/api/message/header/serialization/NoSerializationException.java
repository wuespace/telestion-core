package de.wuespace.telestion.api.message.header.serialization;

import de.wuespace.telestion.api.message.header.Information;

import java.lang.reflect.RecordComponent;

/**
 * Thrown to indicate that the {@link Information Information methods}
 * tried to encode or decode values from or to message headers
 * but the record component has no serialization information.
 *
 * If that exception occurs, please check that all record components have
 * serialization information before continuing.
 *
 * @see Information
 * @author Ludwig Richter (@fussel178)
 */
public class NoSerializationException extends RuntimeException {
	private final RecordComponent component;

	/**
	 * Constructs a {@code NoSerializationException} with the record component
	 * missing serialization information and an additional cause.
	 * @param component the record component missing the serialization information
	 * @param cause an additional cause
	 */
	public NoSerializationException(RecordComponent component, Throwable cause) {
		super("Record component " + component.getName() + " has no Serialization Information. " +
						"Please add Serialization information (@SerializationInfo).", cause);
		this.component = component;
	}

	/**
	 * Constructs a {@code NoSerializationException} with the record component
	 * missing serialization information.
	 * @param component the record component missing the serialization information
	 */
	public NoSerializationException(RecordComponent component) {
		this(component, null);
	}

	/**
	 * Returns the record component missing serialization information which
	 * causes this exception to be thrown.
	 * @return the record component missing serialization information
	 */
	public RecordComponent getComponent() {
		return this.component;
	}
}