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
	 * <p>
	 * For example:
	 * <pre>
	 * {@code
	 * // here is your loader list
	 * List<Loader> loaders;
	 *
	 * // the Event Selector is basically a lambda function which selects the event
	 * // on a loader that you want to call
	 * Loader.call(loaders, (loader, promise) -> loader.onBeforeVertxStartup(promise));
	 *
	 * // due to the nature of both definitions, you can use the double-colon operator (::)
	 * // or method reference
	 * Loader.call(loaders, Loader::onBeforeVertxStartup);
	 * }
	 * </pre>
	 *
	 * @param loader  the loader which provides the selected event handler
	 * @param promise a promise which receives the selected event handler
	 * @see Loader#call(List, EventSelector)
	 */
	void handle(Loader<?> loader, Promise<Void> promise) throws Exception;
}
