package de.wuespace.telestion.services.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import de.wuespace.telestion.api.message.JsonMessage;

import java.util.Collections;
import java.util.List;

/**
 * Record to provide the structure of a database request.
 *
 * @param collection			class of the data type
 * @param query				MongoDb query looks like this: { key: value }
 *                          with key meaning the name of the field in the document.
 *                          IN condition: { key: { $in: ["value1", "value2", ...] }}
 *                          AND condition: { key1: "value1", key2: { $lt: value } } with $lt meaning less than
 *                          OR condition: { $or: [{ key1: "value1" }, { key2: { $gt: value2 }}] }
 * @see <a href="https://docs.mongodb.com/manual/tutorial/query-documents/">MongoDB manual</a> for more information
 */
public record DbRequest(
		@JsonProperty String collection,
		@JsonProperty JsonObject query,
		@JsonProperty List<String> fields,
		@JsonProperty List<String> sort,
		@JsonProperty int limit,
		@JsonProperty int skip) implements JsonMessage {
			private DbRequest() {
				this("", new JsonObject(), Collections.emptyList(), Collections.emptyList(), -1, 0);
			}

			public DbRequest(String collection) {
				this(collection, new JsonObject(), Collections.emptyList(), Collections.emptyList(), -1, 0);
			}

			public DbRequest(String collection, JsonObject query) {
				this(collection, query, Collections.emptyList(), Collections.emptyList(), -1, 0);
			}
}
