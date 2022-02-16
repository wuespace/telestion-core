package de.wuespace.telestion.application.launcher;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * An extended version of the {@link Launcher}.
 * <p>
 * This launcher controls and runs a {@link Vertx} instance configured with {@link VertxOptions}.
 *
 * @author Ludwig Richter (@fussel178)
 * @see Launcher
 * @see Vertx
 * @see VertxOptions
 */
public interface VertxLauncher extends Launcher {

	/**
	 * Get the {@link VertxOptions} for the running {@link Vertx} instance.
	 *
	 * @return the configuration options for the Vertx instance
	 */
	VertxOptions getVertxOptions();

	/**
	 * A reference to the running {@link Vertx} instance in the launcher.
	 *
	 * @return a reference to the Vertx instance
	 */
	Vertx getVertx();
}
