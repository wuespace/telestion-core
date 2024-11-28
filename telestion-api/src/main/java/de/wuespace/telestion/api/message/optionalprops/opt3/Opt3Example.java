package de.wuespace.telestion.api.message.optionalprops.opt3;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Opt3Example(@JsonProperty @OptionalBool(defaultBool = true) boolean bool,
						  @JsonProperty @OptionalFloatingPoint(defaultFloat = 0.0) float f,
						  @JsonProperty @OptionalFloatingPoint(defaultFloat = 0.1) double d,
						  @JsonProperty @OptionalInteger(defaultInteger = 10) long l,
						  @JsonProperty @OptionalInteger(defaultInteger = -1204) int i,
						  @JsonProperty @OptionalString(defaultString = "cb0s Was Here :D") String s,
						  // It might be beneficial to add one more annotation for chars
						  @JsonProperty @OptionalInteger(defaultInteger = 65) char c) {
}
