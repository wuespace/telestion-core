package de.wuespace.telestion.application.loader;

import io.vertx.core.Promise;

import java.util.List;

/**
 * Selects and executes an event handler on a {@link Loader}.
 *
 * @author Ludwig Richter (@fussel178)
 * @see Loader#call(List, EventSelector)
 */
@FunctionalInterface
public interface EventSelector {
	/**
	 * Selects and executes an event handler on a {@link Loader}.
	 *
	 * @param loader  the loader which provides the selected event handler
	 * @param promise a promise which receives the selected event handler
	 * @see Loader#call(List, EventSelector)
	 */
	void handle(Loader<?> loader, Promise<Void> promise) throws Exception;
}
