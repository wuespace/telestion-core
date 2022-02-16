package de.wuespace.telestion.application.launcher;

import java.nio.file.Path;

/**
 * An extended version of the {@link Launcher}.
 * <p>
 * This launcher uses a configuration file to configure itself.
 *
 * @param <T> the type of the launcher configuration
 * @author Ludwig Richter (@fussel178)
 * @see Launcher
 */
public interface ConfigLauncher<T> extends Launcher {

	/**
	 * Get the path to the configuration file for the launcher.
	 *
	 * @return the path to the configuration file
	 */
	Path getMainConfigPath();

	/**
	 * Get the current configuration of the launcher.
	 *
	 * @return the current configuration of the launcher
	 */
	T getMainConfiguration();
}
