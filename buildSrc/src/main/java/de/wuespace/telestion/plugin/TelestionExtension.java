package de.wuespace.telestion.plugin;

import org.gradle.api.provider.Property;

public abstract class TelestionExtension {
	/**
	 * Returns the project description mainly used for packaging metadata.
	 * @return the project description
	 */
	abstract public Property<String> getDescription();

	/**
	 * Returns the project pretty name.
	 * This name is mainly useful for documentation and metadata purposes.
	 * @return the pretty name of the project
	 */
	abstract public Property<String> getPrettyName();
}
