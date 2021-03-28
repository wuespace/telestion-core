package org.telestion.core.connection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.telestion.api.message.JsonMessage;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="className")
public interface ConnectionDetails extends JsonMessage {

}
