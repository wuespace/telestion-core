package de.wuespace.telestion.api.message.optionalprops.opt4;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Opt4Example(@JsonProperty @OptionalJsonProperty(defaultVal = "34.1") float f,
						  @JsonProperty @OptionalJsonProperty(defaultVal = "123") int i,
						  @JsonProperty @OptionalJsonProperty(defaultVal = "This is a proper String") String s) {
	/*
	 * With this solution, the config loader would automatically parse those values to the designated data types.
	 * This works around the problem imposed by the static type system by java, but could be notably slower.
	 */
}
