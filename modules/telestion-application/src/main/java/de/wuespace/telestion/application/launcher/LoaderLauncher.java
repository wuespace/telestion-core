package de.wuespace.telestion.application.launcher;

import de.wuespace.telestion.application.loader.Loader;

import java.util.List;

/**
 * An extended version of the {@link VertxLauncher}.
 * <p>
 * The launcher uses {@link Loader Loaders} to run user defined hooks on Launcher and
 * {@link io.vertx.core.Vertx Vertx} events.
 *
 * @author Ludwig Richter (@fussel178)
 * @see VertxLauncher
 * @see Loader
 */
public interface LoaderLauncher extends VertxLauncher {

	/**
	 * Get a list of loaders that contain the user defined hooks.
	 *
	 * @return a list of loaders with user defined hooks
	 */
	List<Loader<?>> getLoaders();
}
