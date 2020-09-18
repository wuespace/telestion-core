package org.telestion.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonObject;

/**
 * The base configuration of verticles.
 *
 * @author Jan von Pichowski
 */
@SuppressWarnings("preview")
public record VerticleConfig(
        @JsonProperty String name,
        @JsonProperty String verticle,
        @JsonProperty int magnitude,
        @JsonProperty JsonNode config) {

    /**
     * Only for deserialization
     */
    @SuppressWarnings("unused")
    private VerticleConfig(){
        this(null, null, 0, null);
    }

    /**
     *
     * @return the json representation of this record
     */
    public JsonObject json(){
        return JsonObject.mapFrom(this);
    }

    /**
     *
     * @return the json representation of the config node
     */
    public JsonObject jsonConfig(){
        return new JsonObject(config().toString());
    }
}
