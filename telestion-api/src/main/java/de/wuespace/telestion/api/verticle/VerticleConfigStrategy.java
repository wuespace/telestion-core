package de.wuespace.telestion.api.verticle;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * A strategy to handle the configuration of .
 * @param <T> the type of the configuration
 *
 * @author Ludwig Richter (@fussel178), Pablo Klaschka (@pklaschka)
 */
public class VerticleConfigStrategy<T extends TelestionConfiguration> {
	private final T config;
	private final JsonObject untypedConfig;
	private final T defaultConfig;
	private final JsonObject untypedDefaultConfig;

	/**
	 * Creates a new configuration strategy for a verticle.
	 * The default configuration gets inferred from the {@code configType} parameter.
	 * The inferred default configuration is used to fill the configuration with default values.
	 *
	 * @param untypedConfig the untyped configuration
	 * @param configType the type of the configuration
	 */
	public VerticleConfigStrategy(JsonObject untypedConfig, Class<T> configType) {
		this(untypedConfig, getDefaultUntypedConfig(configType), configType);
	}

	/**
	 * Creates a new configuration strategy for a verticle.
	 * The {@code untypedDefaultConfig} parameter is used to fill the configuration with default values.
	 * @param untypedConfig the untyped configuration
	 * @param untypedDefaultConfig the default untyped configuration
	 * @param configType the type of the configuration
	 */
	public VerticleConfigStrategy(JsonObject untypedConfig, JsonObject untypedDefaultConfig, Class<T> configType) {
		assertConfigTypeNonNull(configType);

		this.untypedConfig = untypedConfig;
		this.untypedDefaultConfig = untypedDefaultConfig;
		this.defaultConfig = Objects.isNull(untypedDefaultConfig) ? null : untypedDefaultConfig.mapTo(configType);

		var combined = new JsonObject().mergeIn(untypedDefaultConfig).mergeIn(untypedConfig);
		this.config = combined.mapTo(configType);
	}

	/**
	 * Returns the configuration type of the verticle class.
	 * @param clazz the verticle class extending {@link TelestionVerticle}
	 * @return the configuration type class name of the verticle class
	 * @param <K> the configuration type of the verticle class
	 * @param <T> the verticle class extending {@link TelestionVerticle}
	 */
	@SuppressWarnings("unchecked")
	public static <K extends TelestionConfiguration, T extends TelestionVerticle<K>> Class<K> getConfigType(Class<T> clazz) {
		try {
			var genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
			var configurationTypeArgument = genericSuperclass.getActualTypeArguments()[0];
			var className = configurationTypeArgument.getTypeName();
			Class<?> configClass = Class.forName(className);
			return (Class<K>) configClass;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the default configuration of the verticle class.
	 * @param configType the configuration type of the verticle class
	 * @return the default configuration of the verticle class. {@code null} if the configuration class has no default constructor.
	 * @param <T> the configuration type of the verticle class
	 */
	private static <T extends TelestionConfiguration> JsonObject getDefaultUntypedConfig(Class<T> configType) {
		try {
			var defaultConfig = configType.getConstructor().newInstance();
			return defaultConfig.toJsonObject();
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
				 IllegalAccessException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Get the verticle configuration in the Configuration type format.
	 *
	 * @return the verticle configuration
	 */
	public T getConfig() {
		assertConfigObjectNonNull(config);
		return config;
	}

	/**
	 * Get the verticle configuration in a generic format.
	 *
	 * @return the verticle configuration
	 */
	public JsonObject getUntypedConfig() {
		assertConfigObjectNonNull(untypedConfig);
		return untypedConfig;
	}

	/**
	 * Get the default verticle configuration in the Configuration type format.
	 *
	 * @return the default verticle configuration
	 */
	public T getDefaultConfig() {
		assertConfigObjectNonNull(defaultConfig);
		return defaultConfig;
	}

	/**
	 * Get the default verticle configuration in a generic format.
	 *
	 * @return the default verticle configuration
	 */
	public JsonObject getUntypedDefaultConfig() {
		assertConfigObjectNonNull(untypedDefaultConfig);
		return untypedDefaultConfig;
	}

	/**
	 * Asserts that the configuration is not null.
	 * @param object the object to check
	 * @throws IllegalStateException if the object is null
	 */
	private void assertConfigObjectNonNull(Object object) {
		if (Objects.isNull(object)) {
			throw new IllegalStateException("Trying to access config that is null");
		}
	}

	/**
	 * Asserts that the configuration type is not null.
	 * @param configType the configuration type to check
	 * @throws IllegalStateException if the configuration type is null
	 */
	private void assertConfigTypeNonNull(Class<T> configType) {
		if (Objects.isNull(configType)) {
			throw new IllegalArgumentException("Config type must not be null");
		}
	}
}
