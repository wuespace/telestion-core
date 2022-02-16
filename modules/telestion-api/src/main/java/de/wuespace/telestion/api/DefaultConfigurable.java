package de.wuespace.telestion.api;

import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.json.JsonObject;

/**
 * Classes implementing {@link DefaultConfigurable} have a default and a current configuration.
 * <p>
 * Both configurations can change during runtime.
 * The current configuration change externally and is not writable by the user.
 * <p>
 * Default and current configuration are merged on a change.
 * When entries collide during merge, the value from the current configuration is preferred.
 * <p>
 * Both current and default configuration are accessible in the dynamic {@link JsonObject} format
 * and the strictly typed {@link JsonMessage} format.
 *
 * @param <T> the type of the strictly typed {@link JsonMessage} configuration format
 * @author Ludwig Richter (@fussel178)
 */
public interface DefaultConfigurable<T extends JsonMessage> {

	/**
	 * Set the default configuration and merge with the default configuration of the configurable class.
	 *
	 * @param defaultConfig the new default verticle configuration
	 */
	void setDefaultConfig(JsonObject defaultConfig);

	/**
	 * Set the default configuration and merge with the default configuration of the configurable class.
	 *
	 * @param defaultConfig the new default verticle configuration
	 */
	void setDefaultConfig(T defaultConfig);

	/**
	 * Get the default configuration in a dynamic {@link JsonObject} format.
	 *
	 * @return the default configuration
	 */
	JsonObject getGenericDefaultConfig();

	/**
	 * Get the default configuration in a strictly types {@link JsonMessage} format.
	 *
	 * @return the default configuration
	 */
	T getDefaultConfig();

	/**
	 * Get the current configuration in a dynamic {@link JsonObject} format.
	 *
	 * @return the current configuration
	 */
	JsonObject getGenericConfig();

	/**
	 * Get the current configuration in a strictly types {@link JsonMessage} format.
	 *
	 * @return the current configuration
	 */
	T getConfig();
}
