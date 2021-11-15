package de.wuespace.telestion.api.config;

import io.vertx.core.json.JsonObject;

/**
 * A utility class for the selection of configuration files.
 *
 * @author Jan von Pichowski
 */
public final class Config {

	/**
	 * Selects the right configuration file.
	 *
	 * @param forcedConfig this config will be applied if it is not null
	 * @param config       this is common config which represents the config class in json. If fields are not available
	 *                     they will be set like the default values of the class when the default constructor is called.
	 * @param type         the type of the config.
	 * @param <T>          defines the class type of the configuration
	 * @return the selected configuration
	 */
	public static <T> T get(T forcedConfig, JsonObject config, Class<T> type) {
		return forcedConfig == null ? config.mapTo(type) : forcedConfig;
	}

	/**
	 * Selects the right configuration file.
	 *
	 * @param forcedConfig  this config will be applied if it is not null
	 * @param defaultConfig this config contains the default values which replace absent values in the JSON config
	 * @param jsonConfig    this is common config which represents the config class in json. If fields are not available
	 *                      they will be set like the default values of the class when the default constructor is called.
	 * @param <T>           defines the class type of the configuration
	 * @return the selected configuration
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(T forcedConfig, T defaultConfig, JsonObject jsonConfig) {
		var type = (Class<T>) defaultConfig.getClass();
		var config = JsonObject.mapFrom(defaultConfig).mergeIn(jsonConfig);
		return forcedConfig == null ? config.mapTo(type) : forcedConfig;
	}
}
