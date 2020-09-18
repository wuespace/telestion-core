package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telestion.api.message.JsonMessage;

@SuppressWarnings("preview")
public record TcpConnected(@JsonProperty String host, @JsonProperty int port) implements JsonMessage {
    @SuppressWarnings("unused")
    private TcpConnected(){
        this(null, 0);
    }
}
