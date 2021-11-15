package de.wuespace.telestion.services.connection.legacy;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

/**
 * Connection data from the serial connection ({@link SerialConn}) which will be serialized into json to put it on the
 * vertx event bus.<br>
 * It is part of the old connection api and will be removed in v0.7.
 *
 * @author Jan von Pichowski (jvpichowski)
 * @deprecated will be removed in v0.7
 */
@Deprecated(since = "v0.1.3", forRemoval = true)
public record SerialData(@JsonProperty byte[] data) implements JsonMessage {

	@SuppressWarnings("unused")
	private SerialData(){
		this(null);
	}
}
