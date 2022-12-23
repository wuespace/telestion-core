package de.wuespace.telestion.services.connection.rework;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.wuespace.telestion.api.message.JsonRecord;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="className")
public interface ConnectionDetails extends JsonRecord {

}
