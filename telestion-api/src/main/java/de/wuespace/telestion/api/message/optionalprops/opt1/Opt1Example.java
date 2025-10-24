package de.wuespace.telestion.api.message.optionalprops.opt1;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Opt1Example(
		@JsonProperty @OptionalJsonProperty(defaultString = "foo") String foo,
		// We can even give global default parameters by inferring them into @OptionalJsonProperty
		@JsonProperty @OptionalJsonProperty int bar,
		@JsonProperty
		boolean extraBar,	// Not everything must be optional
		@JsonProperty @OptionalJsonProperty(defaultChar = 'F')
		char extraFoo) {
}
