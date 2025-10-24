package de.wuespace.telestion.api.message.optionalprops.opt2;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Opt2Example(@JsonProperty @OptionalString(defaultString = "foo") String s,
						  @JsonProperty @OptionalBool(defaultBool = false) boolean bool,
						  @JsonProperty @OptionalByte(defaultByte = 127) byte b,
						  @JsonProperty @OptionalInt(defaultInt = -1) int i) {
	/*
	 * Note that this implementation could also be improved by adding global default parameters to each of the
	 * annotations as it was shown by example 1.
	 */
}
