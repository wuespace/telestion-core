package org.telestion.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

@SuppressWarnings("preview")
public record TcpData(
        @JsonProperty String address,
        @JsonProperty int port,
        @JsonProperty byte[] data) implements JsonMessage {

    @SuppressWarnings("unused")
    private TcpData(){
        this(null, 0, null);
    }
}
