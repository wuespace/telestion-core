package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface JsonMessage {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    default String name() {
        return getClass().getSimpleName();
    }
}
