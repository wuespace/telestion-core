package org.telestion.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Configuration(
        @JsonProperty String app_name,
        @JsonProperty List<VerticleConfig> verticles) {

    private Configuration(){
        this(null, null);
    }
}
