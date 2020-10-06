package org.telestion.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The base application configuration object.
 *
 * @author Jan von Pichowski
 */
@SuppressWarnings("preview")
public record Configuration(@JsonProperty String app_name, @JsonProperty List<VerticleConfig> verticles) {

    /**
     * Only for deserialization
     */
    @SuppressWarnings("unused")
    private Configuration() {
        this(null, null);
    }
}
