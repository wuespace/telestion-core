package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

/**
 * <p>
 *     Data-Object for the data which must be sent over a network-connection.<br>
 *     Apart from containing the raw data from the stream, also
 *     connection-details are contained, which allow to determine the target where to send the data to.
 * </p>
 * <p>
 *     The {@link Sender Sender-verticle} will be handling the distribution of the data.
 *     If invalid though the message will not be sent at all and the sending verticle will not be notified, this will
 *     only be logged as an error.
 * </p>
 * <p>
 *     Sending this type of object to a specific sender means that {@link ConnectionDetails} for other senders are
 *     treated as invalid and will be logged as an error.
 * </p>
 *
 * @param rawData		to send
 * @param conDetails	array of connection-details - each element represents one connection where the data will be
 *                      sent to
 *
 * @author Cedric Boes
 * @version 1.0
 */
public record SenderData(@JsonProperty byte[] rawData,
						 @JsonProperty ConnectionDetails... conDetails) implements JsonMessage {

	@SuppressWarnings("unused")
	private SenderData() {
		this(null);
	}

	public static SenderData fromConnectionData(ConnectionData data) {
		return new SenderData(data.rawData(), data.conDetails());
	}
}
