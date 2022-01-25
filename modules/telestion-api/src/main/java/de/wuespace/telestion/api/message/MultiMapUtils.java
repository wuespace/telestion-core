package de.wuespace.telestion.api.message;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utilities for the {@link MultiMap Vert.x multimap}.
 *
 * @author Ludwig Richter (fussel178)
 */
public class MultiMapUtils {

	/**
	 * Merges an array of multi-maps into one multimap
	 * with the {@link MultiMap#addAll(MultiMap) addAll} function.
	 *
	 * @param maps multiple multi-maps that should be merged into one
	 * @return a new multimap with the merged content of the other multi-maps
	 */
	public static MultiMap merge(MultiMap... maps) {
		return merge(Arrays.stream(maps));
	}

	/**
	 * Merges a list of multi-maps into one multimap
	 * with the {@link MultiMap#addAll(MultiMap) addAll} function.
	 *
	 * @param list multiple multi-maps that should be merged into one
	 * @return a new multimap with the merged content of the other multi-maps
	 */
	public static MultiMap merge(List<MultiMap> list) {
		return merge(list.stream());
	}

	/**
	 * Merges a stream of multi-maps into one multimap
	 * with the {@link MultiMap#addAll(MultiMap) addAll} function.
	 *
	 * @param stream a stream that contains multi-maps that should be merged into one
	 * @return a new multimap with the merged content of the other multi-maps
	 */
	public static MultiMap merge(Stream<MultiMap> stream) {
		var finalMap = MultiMap.caseInsensitiveMultiMap();
		stream.forEachOrdered(finalMap::addAll);
		return finalMap;
	}

	/**
	 * Extracts the {@link MultiMap Vert.x headers} from the received {@link Message}.
	 *
	 * @param message the {@link Message} that contains the {@link MultiMap Vert.x headers}
	 * @return the extracted {@link MultiMap Vert.x headers}
	 */
	public static MultiMap from(Message<?> message) {
		return message.headers();
	}

	/**
	 * Extracts the {@link MultiMap Vert.x headers} from the specified {@link DeliveryOptions}.
	 *
	 * @param options the {@link DeliveryOptions} that contain the {@link MultiMap Vert.x headers}
	 * @return the extracted {@link MultiMap Vert.x headers}
	 */
	public static MultiMap from(DeliveryOptions options) {
		return options.getHeaders();
	}
}
