package de.wuespace.telestion.api.message.header;

import de.wuespace.telestion.api.utils.AbstractUtils;
import io.vertx.core.MultiMap;

import java.util.Arrays;

/**
 * Utility class for conveniently adding {@link Information} to a {@link MultiMap header}.
 *
 * @author Cedric Boes (@cb0s), Ludwig Richter (@fussel178)
 */
public class InformationUtils extends AbstractUtils {
	/**
	 * Appends all given {@link Information information objects} to the given {@link MultiMap}.
	 *
	 * @param headers     multi map to append to
	 * @param information {@link Information information-objects} which should be added to the given {@link MultiMap}
	 * @return given {@link MultiMap} headers with appended information
	 */
	public static MultiMap appendAll(MultiMap headers, Information... information) {
		Arrays.stream(information).forEach(e -> e.appendToHeaders(headers));
		return headers;
	}

	/**
	 * Creates a new {@link MultiMap} which is filled with the given {@link Information}.
	 * The new map is then being returned.
	 *
	 * @param information {@link Information information-array} to add to the header
	 * @return new {@link MultiMap} header with the given information-array as content.
	 */
	public static MultiMap allToHeaders(Information... information) {
		return appendAll(MultiMap.caseInsensitiveMultiMap(), information);
	}
}
