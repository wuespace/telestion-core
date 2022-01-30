package de.wuespace.telestion.api.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * An abstract verticle class that you can extend to write your own verticle classes.
 * <p>
 * It's extends the {@link AbstractVerticle} from Vert.x to add support for default configurations
 * and type-safe usage of the configuration JSON object provided by Vert.x.
 *
 * @param <T> the type of your Configuration class
 * @author Cedric Bös (@cb0s), Pablo Klaschka (@pklaschka), Jan von Pichowski (@jvpichowski),
 * Ludwig Richter (@fussel178)
 */
public abstract class TelestionVerticle<T extends TelestionConfiguration> extends AbstractVerticle {
	/**
	 * The default verticle configuration in a generic format.
	 */
	private JsonObject defaultGenericConfig = new JsonObject();
	/**
	 * The default verticle configuration in the Configuration type format.<p>
	 * Is <code>null</code> when no type via {@link #getConfigType()} is given.
	 */
	private T defaultConfig;

	/**
	 * The verticle configuration in a generic format.
	 */
	private JsonObject genericConfig = new JsonObject();
	/**
	 * The verticle configuration in the Configuration type format.<p>
	 * Is <code>null</code> when no type via {@link #getConfigType()} is given.
	 */
	private T config;

	/**
	 * The default logger instance.
	 */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Get the Configuration Class type from the inheriting class.
	 *
	 * @return the Configuration Class type
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getConfigType() {
		try {
			String className = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
			Class<?> clazz = Class.forName(className);
			//noinspection unchecked
			return (Class<T>) clazz;
		} catch (Exception e) {
			logger.warn("Cannot get Class type from generic: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a new Telestion verticle and tries to load the default configuration
	 * from the specified configuration class.
	 *
	 * @param skipDefaultConfigLoading when {@code true} the loading of the default configuration is skipped
	 */
	public TelestionVerticle(boolean skipDefaultConfigLoading) {
		if (skipDefaultConfigLoading) {
			return;
		}
		var configType = getConfigType();
		if (Objects.isNull(configType)) {
			return;
		}

		try {
			var defaultConfig = configType.getConstructor().newInstance();
			this.defaultConfig = defaultConfig;
			this.defaultGenericConfig = defaultConfig.json();
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			// no default configuration on configuration class found, ignoring
			logger.info("No default configuration found for {}. " +
							"Expected constructor with no arguments to exist on {}. " +
							"Continuing without default configuration.",
					getClass().getSimpleName(), getConfigType().getSimpleName());
		}
	}

	/**
	 * Same as {@link TelestionVerticle#TelestionVerticle(boolean)}
	 * but enables loading of default configuration if possible.
	 */
	public TelestionVerticle() {
		this(false);
	}

	@Override
	public final void start(Promise<Void> startPromise) throws Exception {
		updateConfigs();
		// put general startup steps here
		onStart(startPromise);
	}

	// not used but blocked with final
	@Override
	public final void start() throws Exception {
	}

	@Override
	public final void stop(Promise<Void> stopPromise) throws Exception {
		// put general cleanup steps here
		onStop(stopPromise);
	}

	// not used but blocked with final
	@Override
	public final void stop() throws Exception {
	}

	/**
	 * Starts the verticle.
	 * <p>
	 * This is called by Vert.x when the verticle instance is deployed. Please don't call it yourself.
	 * <p>
	 * If your verticle needs a start routine which takes some time to finish,
	 * then complete the start promise some time later.
	 * <p>
	 * This is the asynchronous part to the {@link #onStart()} method.
	 *
	 * @param startPromise a promise which should be called when verticle start is complete
	 */
	public void onStart(Promise<Void> startPromise) throws Exception {
		onStart();
		startPromise.complete();
	}

	/**
	 * Starts the verticle.
	 * <p>
	 * This is called by Vert.x when the verticle instance is deployed. Please don't call it yourself.
	 * <p>
	 * If your verticle only does synchronous start tasks, use this method.
	 * <p>
	 * This is the synchronous part to the {@link #onStart(Promise)} method.
	 */
	public void onStart() throws Exception {
	}

	/**
	 * Stops the verticle.
	 * <p>
	 * This is called by Vert.x when the verticle instance is un-deployed. Please don't call it yourself.
	 * <p>
	 * If your verticle needs a stop routine which takes some time to finish,
	 * then complete the stop promise some time later.
	 * <p>
	 * This is the asynchronous part to the {@link #onStop()} method.
	 *
	 * @param stopPromise a promise which should be called when verticle stop is complete
	 */
	public void onStop(Promise<Void> stopPromise) throws Exception {
		onStop();
		stopPromise.complete();
	}

	/**
	 * Stops the verticle.
	 * <p>
	 * This is called by Vert.x when the verticle instance is un-deployed. Please don't call it yourself.
	 * <p>
	 * If your verticle only does synchronous stop tasks, use this method.
	 * <p>
	 * This is the synchronous part to the {@link #onStop(Promise)} method.
	 */
	public void onStop() throws Exception {
	}

	/**
	 * Set the default verticle configuration and update the verticle configuration.
	 *
	 * @param defaultConfig the new default verticle configuration
	 */
	public void setDefaultConfig(JsonObject defaultConfig) {
		this.defaultGenericConfig = defaultConfig;
		this.defaultConfig = mapToConfiguration(defaultConfig);
		updateConfigs();
	}

	/**
	 * Set the default verticle configuration and update the verticle configuration.
	 *
	 * @param defaultConfig the new default verticle configuration
	 */
	public void setDefaultConfig(T defaultConfig) {
		this.defaultConfig = defaultConfig;
		this.defaultGenericConfig = defaultConfig.json();
		updateConfigs();
	}

	/**
	 * Get the default verticle configuration in the Configuration type format.<p>
	 * Returns <code>null</code> when no type via {@link #getConfigType()} is given.
	 *
	 * @return the default verticle configuration
	 */
	public T getDefaultConfig() {
		return defaultConfig;
	}

	/**
	 * Get the default verticle configuration in a generic format.
	 *
	 * @return the default verticle configuration
	 */
	public JsonObject getGenericDefaultConfig() {
		return defaultGenericConfig;
	}

	/**
	 * Get the verticle configuration in the Configuration type format.<p>
	 * Returns <code>null</code> when no type via {@link #getConfigType()} is given.
	 *
	 * @return the verticle configuration
	 */
	public T getConfig() {
		return config;
	}

	/**
	 * Get the verticle configuration in a generic format.
	 *
	 * @return the verticle configuration
	 */
	public JsonObject getGenericConfig() {
		return genericConfig;
	}

	/**
	 * Block the usage of <code>config()</code> in inheriting classes.
	 *
	 * @return the verticle configuration from vertx merged with the default configuration
	 */
	@Override
	public final JsonObject config() {
		return defaultGenericConfig.mergeIn(super.config());
	}

	/**
	 * Update the config representations based on the default verticle configuration.
	 */
	private void updateConfigs() {
		genericConfig = config();
		config = mapToConfiguration(genericConfig);
	}

	/**
	 * Map a generic JSON object to the Configuration type.<p>
	 * Returns <code>null</code> when no type via {@link #getConfigType()} is given.
	 *
	 * @param object the generic JSON object to map
	 * @return the JSON object in the Configuration type format
	 */
	private T mapToConfiguration(JsonObject object) {
		var type = getConfigType();
		return type != null ? object.mapTo(type) : null;
	}
}
