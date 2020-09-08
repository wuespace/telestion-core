package org.telestion.adapter.mavlink;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("preview")
public record AddressPort(
        @JsonProperty String address,
        @JsonProperty int port) {

    @SuppressWarnings("unused")
    private AddressPort(){
        this(null, 0);
    }
}
