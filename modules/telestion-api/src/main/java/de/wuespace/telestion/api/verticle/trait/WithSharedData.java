package de.wuespace.telestion.api.verticle.trait;

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

	/**
	 * Return the default local map for this specific verticle.
	 * @param <K> the type of the key
	 * @param <V> the type of the value
	 * @return the default local map for this specific verticle
	 */
	default <K, V> LocalMap<K, V> defaultLocalMap() {
		return localMap(defaultStorageKey());
	}

	/**
	 * Return the default remote map for this specific verticle.
	 * @param <K> the type of the key
	 * @param <V> the type of the value
	 * @return the default remote map for this specific verticle
	 */
	default <K, V> Future<AsyncMap<K, V>> defaultRemoteMap() {
		return remoteMap(defaultStorageKey());
	}

	/**
	 * Return the default storage key for this specific verticle
	 * used for the {@link #defaultLocalMap()} and {@link #defaultRemoteMap()} verticle storage spaces.
	 * @return the default storage key for this specific verticle
	 */
	default String defaultStorageKey() {
		return getClass().getName();
	}
}
