package org.telestion.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonObject;

public record VerticleConfig(
        @JsonProperty String name,
        @JsonProperty String verticle,
        @JsonProperty int magnitude,
        @JsonProperty JsonNode config) {

    private VerticleConfig(){
        this(null, null, 0, null);
    }

    public JsonObject json(){
        return JsonObject.mapFrom(this);
    }

    public JsonObject jsonConfig(){
        return new JsonObject(config().toString());
    }
}
