package de.wuespace.telestion.services.loader.verticle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.json.JsonObject;

/**
 * @author Jan von Pichowski (@jvpichowski), Ludwig Richter (@fussel178)
 */
public record VerticleConfiguration(
		@JsonProperty String name,
		@JsonProperty String verticle,
		@JsonProperty int magnitude,
		@JsonProperty JsonNode config) implements JsonMessage {

	public static VerticleConfiguration from(JsonObject jsonObject) throws IllegalArgumentException {
		return jsonObject.mapTo(VerticleConfiguration.class);
	}

	public JsonObject jsonConfig() {
		return new JsonObject(config().toString());
	}
}
