package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.telestion.api.message.JsonMessage;

/**
 * @param dataType			class of the data type
 * @param query				MongoDb query looks like this: { key: value }
 *                          with key meaning the name of the field in the document.
 *                          IN condition: { key: { $in: ["value1", "value2", ...] }}
 *                          AND condition: { key1: "value1", key2: { $lt: value } } with $lt meaning less than
 *                          OR condition: { $or: [{ key1: "value1" }, { key2: { $gt: value2 }}] }
 * @see <a href="https://docs.mongodb.com/manual/tutorial/query-documents/">MongoDB manual</a> for more information
 */
public record DbRequest(
		@JsonProperty Class<?> dataType,
		@JsonProperty JsonObject query) implements JsonMessage {
			private DbRequest() {
				this(null, new JsonObject());
			}
}
