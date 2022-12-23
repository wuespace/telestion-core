package de.wuespace.telestion.api.verticle;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Optional;

public class VerticleConfigStrategy<T extends TelestionConfiguration> {

	/**
	 * The default verticle configuration in a generic format.
	 */
	private final JsonObject untypedDefaultConfig;

	/**
	 * The default verticle configuration in the Configuration type format.<p>
	 * Is <code>null</code> when no type for the configuration is given.
	 */
	private final T defaultConfig;

	/**
	 * The verticle configuration in a generic format.
	 */
	private final JsonObject untypedConfig;

	/**
	 * The verticle configuration in the Configuration type format.<p>
	 * Is <code>null</code> when no type for the configuration is given.
	 */
	private final T config;

	public VerticleConfigStrategy(JsonObject verticleConfig, JsonObject untypedDefaultConfig, Class<? extends TelestionVerticle<?>> verticleClazz) {
		var combined = new JsonObject();
		if (untypedDefaultConfig != null) {
			combined.mergeIn(untypedDefaultConfig);

			this.untypedDefaultConfig = untypedDefaultConfig;
			this.defaultConfig = verticleClazz != null ? untypedDefaultConfig.mapTo(verticleClazz) : null;
		} else {
			this.untypedDefaultConfig = null;
			this.defaultConfig = null;
		}

		combined.mergeIn(verticleConfig);
		this.untypedConfig = combined;
		this.config = verticleClazz != null ? combined.mapTo(verticleClazz) : null;
	}

	public VerticleConfigStrategy(JsonObject verticleConfig, T defaultConfig, Class<? extends TelestionVerticle<?>> verticleClazz) {
		this(verticleConfig, defaultConfig != null ? defaultConfig.toJsonObject() : null, configType);
	}

	public VerticleConfigStrategy(JsonObject verticleConfig, Class<? extends TelestionVerticle<?>> verticleClazz) {
		this(verticleConfig, getDefaultConfigFromConfigType(configType), configType);
	}

	/**
	 * Get the default verticle configuration in the Configuration type format.
	 *
	 * @return the default verticle configuration
	 */
	public T getDefaultConfig() throws IllegalStateException {
		if (Objects.isNull(defaultConfig)) {
			throw new IllegalStateException("Tried to access getDefaultConfig(), but no configType was defined.");
		}

		return defaultConfig;
	}

	/**
	 * Get the default verticle configuration in an untyped/dynamic format.
	 *
	 * @return the default verticle configuration
	 */
	public JsonObject getUntypedDefaultConfig() {
		return untypedDefaultConfig;
	}

	/**
	 * Get the verticle configuration in the Configuration type format.
	 *
	 * @return the verticle configuration
	 */
	public T getConfig() throws IllegalStateException {
		if (Objects.isNull(config)) {
			throw new IllegalStateException("Tried to access getConfig(), but no configType was defined.");
		}

		return config;
	}

	/**
	 * Get the verticle configuration in an untyped/dynamic format.
	 *
	 * @return the verticle configuration
	 */
	public JsonObject getUntypedConfig() {
		return untypedConfig;
	}

	private static <T extends TelestionConfiguration> T getDefaultConfigFromConfigType(Class<T> configType) {
		try {
			return configType != null ? configType.getConstructor().newInstance() : null;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * Get the Configuration Class type from the inheriting class.
	 *
	 * @return the Configuration Class type
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends TelestionConfiguration> Class<T> getConfigType(Class<? extends TelestionVerticle<?>> clazz) {
		try {
			String className = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
			Class<?> configClazz = Class.forName(className);
			return (Class<T>) configClazz;
		} catch (Exception e) {
			return null;
		}
	}
}
