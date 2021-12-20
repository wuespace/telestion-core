package de.wuespace.telestion.example.information;

import de.wuespace.telestion.api.message.header.Information;
import de.wuespace.telestion.api.message.header.serialization.SerializationInfo;
import io.vertx.core.MultiMap;

public record SimpleInformation(
		@SerializationInfo(name = "value1", defaultValue = "4") int value1,
		@SerializationInfo(name = "value2") Integer value2,
		@SerializationInfo(name = "char1") char char1,
		@SerializationInfo(name = "char2") Character char2,
		@SerializationInfo(name = "str") String str
) implements Information {
	// fromHeaders(MultiMap) is optional but improves the developer experience in our opinion
	public static SimpleInformation fromHeaders(MultiMap headers) {
		return Information.fromHeaders(headers, SimpleInformation.class);
	}
}
