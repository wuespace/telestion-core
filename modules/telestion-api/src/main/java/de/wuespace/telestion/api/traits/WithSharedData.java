package de.wuespace.telestion.api.traits;

import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.LocalMap;

/**
 * Allows {@link Verticle} instances to get simplified access to Vert.x shared data
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public class MyVerticle extends TelestionVerticle implements WithSharedData {
 *     @Override
 *     public void startVerticle() {
 *         localMap("abc").put("test", "123");
 *     }
 * }
 * }
 * </pre>
 *
 * @author Pablo Klaschka, Ludwig Richter
 */
public interface WithSharedData extends Verticle {
	/**
	 * Return a local map from the specified storage space.
	 * @param storageSpace the storage space of the local map
	 * @param <K> the type of the key
	 * @param <V> the type of the value
	 * @return the local map
	 */
	default <K, V> LocalMap<K, V> localMap(String storageSpace) {
		return getVertx().sharedData().getLocalMap(storageSpace);
	}

	/**
	 * Return a remote map from the specified storage space.
	 * @param storageSpace the storage space of the remote map
	 * @param <K> the type of the key
	 * @param <V> the type of the value
	 * @return a future which resolves with the remote map
	 */
	default <K, V> Future<AsyncMap<K, V>> remoteMap(String storageSpace) {
		return getVertx().sharedData().getAsyncMap(storageSpace);
	}
}
