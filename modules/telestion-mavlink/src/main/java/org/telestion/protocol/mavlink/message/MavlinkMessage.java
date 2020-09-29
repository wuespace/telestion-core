package org.telestion.protocol.mavlink.message;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import org.telestion.api.message.JsonMessage;
import org.telestion.protocol.mavlink.annotation.MavArray;
import org.telestion.protocol.mavlink.annotation.MavField;
import org.telestion.protocol.mavlink.annotation.MavInfo;
import org.telestion.protocol.mavlink.exception.AnnotationMissingException;
import org.telestion.protocol.mavlink.security.X25Checksum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Superclass for all MavliinkMessages.<br>
 * Implementations will be created automatically by the xml2record-tool.
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public interface MavlinkMessage<T> extends JsonMessage {
	
	/**
	 * Returns the actual size of a message-object.
	 * 
	 * @return actual size
	 */
	@JsonProperty(access = Access.READ_ONLY)
	public default int length() {
		return Arrays.stream(this.getClass().getRecordComponents())
				.filter(component -> component.isAnnotationPresent(MavField.class))
				.filter(component -> {
					try {
						return component.getAccessor().invoke(this) != null;
					} catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
						return false;
					}
				})
				.mapToInt(MavlinkMessage::calcRecordLength)
				.sum();

	}
	
	/**
	 * Checks if the necessary {@link MavInfo MavInfo-Annotation} is present.<br>
	 * If so a representing Object will be returned otherwise an {@link AnnotationMissingException} will be thrown.
	 * 
	 * @return {@link MavInfo} of this class if present
	 * @throws AnnotationMissingException if {@link MavInfo} is not present
	 * @implNote Should not be overwritten!
	 */
	@JsonProperty(access = Access.READ_ONLY)
	default MavInfo checkAnnotation() {
		if (!this.getClass().isAnnotationPresent(MavInfo.class))
			throw new AnnotationMissingException("Required Annotation @MavInfo is missing!");
		else
			return this.getClass().getAnnotation(MavInfo.class);
	}
	
	/**
	 * Returns the CRC_EXTRA-Byte for the {@link MavlinkMessage} declared in the {@link MavInfo}.<br>
	 * It is used for the checksum-calculation based on the {@link X25Checksum} algorithm (also know as
	 * <code>CRC-16-CCITT</code>).<br>
	 * <br>
	 * For more information see:<br>
	 * <a href="https://mavlink.io/en/guide/serialization.html#crc_extra">
	 * https://mavlink.io/en/guide/serialization.html#crc_extra</a>
	 * 
	 * @return CRC_EXTRA-Byte for the {@link MavlinkMessage}
	 * @implNote Should not be overwritten!
	 */
	@JsonProperty(access = Access.READ_ONLY)
	public default int getCrc() {
		return checkAnnotation().crc();
	}
	
	/**
	 * Returns the id of the {@link MavlinkMessage} declared in the {@link MavInfo}.<br>
	 * it is used to identify the message which was sent to cast the payload.<br>
	 * <br>
	 * For more information see:<br>
	 * <a href="https://mavlink.io/en/guide/serialization.html">https://mavlink.io/en/guide/serialization.html</a>
	 * 
	 * @return id of the {@link MavlinkMessage}
	 * @implNote Should not be overwritten!
	 */
	@JsonProperty(access = Access.READ_ONLY)
	public default int getId() {
		return checkAnnotation().id();
	}
	
	/**
	 * Returns the minimum length of this message with all extension excluded.<br>
	 * <br>
	 * <em>Note if {@link #minLength()} and {@link #maxLength()} are equal, there are no extensions!</em> 
	 * 
	 * @return minimum length of message
	 */
	@JsonProperty(access = Access.READ_ONLY)
	public static int minLength() {
		return Arrays.stream(MethodHandles.lookup().lookupClass().getRecordComponents())
				.filter(component -> component.isAnnotationPresent(MavField.class))
				.filter(component -> component.getAnnotation(MavField.class).extension() == false)
				.mapToInt(MavlinkMessage::calcRecordLength)
				.sum();
	}
	
	/**
	 * Returns the maximum length of this message with all extensions in use.<br>
	 * <br>
	 * <em>Note if {@link #minLength()} and {@link #maxLength()} are equal, there are no extensions!</em> 
	 * 
	 * @return maximum length of message
	 */
	@JsonProperty(access = Access.READ_ONLY)
	public static int maxLength() {
		return Arrays.stream(MethodHandles.lookup().lookupClass().getRecordComponents())
					.filter(component -> component.isAnnotationPresent(MavField.class))
					.mapToInt(MavlinkMessage::calcRecordLength)
					.sum();
	}
	
	/**
	 * Calculates the length of a {@link RecordComponent} from a {@link MavlinkMessage}.<br>
	 * If the component is an array the size will be calculated with respect to its length.
	 * 
	 * @param c RecordComponent for calculation
	 * @return MAVLink-length of the RecordComponent
	 */
	@JsonProperty(access = Access.READ_ONLY)
	private static int calcRecordLength(RecordComponent c) {
		int multiplier = c.isAnnotationPresent(MavArray.class) ? c.getAnnotation(MavArray.class).length() : 1;
		return multiplier * c.getAnnotation(MavField.class).nativeType().size;
	}
	
}
