package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonRecord;

/**
 * <p>
 *     Data-Object for sending received data from incoming connections over the
 *     {@link io.vertx.core.eventbus.EventBus EventBus}.<br>
 *     Apart from containing the raw data from the stream, also
 *     connection-details are contained, which (if needed) allow for connection-specific operations, e.g. for sending
 *     responses to the sender.
 * </p>
 * <p>
 *     IncomingData-objects will only be checked by the parsers which might be added to the
 *     {@link io.vertx.core.eventbus.EventBus EventBus}. To send them to specific parsers, the output-address of the
 *     receiver-verticles must be equivalent to the incoming-addresses of the parser-verticles.
 * </p>
 *
 * @param rawData from the stream
 * @param conDetails from the header which might be important to the handlers
 *
 * @author Cedric Boes
 * @version 1.0
 */
public record ConnectionData(@JsonProperty byte[] rawData,
							 @JsonProperty ConnectionDetails conDetails) implements JsonRecord {

	private ConnectionData() {
		this(null, null);
	}
}
