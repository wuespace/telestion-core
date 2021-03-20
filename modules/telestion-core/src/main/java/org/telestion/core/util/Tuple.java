package org.telestion.core.util;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple implementation of a Value-Pair whose values are linked together without any key - value relationship.
 *
 * @param <T1>	Type 1
 * @param <T2>	Type 2
 *
 * @param value1	Value of Type 1
 * @param value2	Value of Type 2
 *
 * @author Cedric Boes
 * @version 1.0
 */
public record Tuple<T1, T2>(
		@JsonProperty T1 value1,
		@JsonProperty T2 value2) {

	/**
	 * This is only for reflection of Config-Loading!
	 */
	@SuppressWarnings("unused")
	private Tuple() {
		this(null, null);
	}

}
