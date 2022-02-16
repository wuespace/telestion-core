package de.wuespace.telestion.application.loader;

import de.wuespace.telestion.application.deployment.Deployment;
import de.wuespace.telestion.application.launcher.ConfigDeploymentLauncher;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * An abstract reference implementation of a {@link Loader}.
 * <p>
 * Use this as a starting point for your own loader.
 *
 * @param <T> the type of the loader configuration
 * @author Ludwig Richter (@fussel178)
 * @see Loader
 * @see LoaderConfiguration
 * @see ConfigDeploymentLauncher
 */
public abstract class TelestionLoader<T extends LoaderConfiguration> implements Loader<T> {

	/**
	 * The default loader configuration in the raw format.e.printStackTrace();
	 */
	private JsonObject genericDefaultConfig = new JsonObject();

	/**
	 * The default loader configuration in the type-safe format.
	 */
	private T defaultConfig;

	/**
	 * The loader configuration in the raw format.
	 */
	private JsonObject genericConfig = new JsonObject();

	/**
	 * The loader configuration in the type-safe format.
	 */
	private T config;

	/**
	 * The reference to the {@link ConfigDeploymentLauncher Launcher} instance which deployed this loader.
	 */
	protected ConfigDeploymentLauncher<? extends JsonObject> launcher;

	/**
	 * Map a generic JSON object to the Configuration type.<p>
	 * Returns <code>null</code> when no type via {@link #getConfigType()} is given.
	 *
	 * @param object the generic JSON object to map
	 * @return the JSON object in the Configuration type format
	 */
	private T mapToConfiguration(JsonObject object) {
		var type = getConfigType();
		return Objects.isNull(type) ? null : object.mapTo(type);
	}

	/**
	 * Merges in the raw configuration into the default configuration
	 * and updates both raw and type-safe configuration.
	 *
	 * @param rawConfig the raw configuration to merge into the default configuration
	 */
	private void mergeInConfiguration(JsonObject rawConfig) {
		this.genericConfig = this.genericDefaultConfig.mergeIn(rawConfig);
		this.config = mapToConfiguration(this.genericConfig);
	}

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
	 * Creates a new Telestion Loader and tries to load the default configuration
	 * from the specified configuration class.
	 *
	 * @param skipDefaultConfigLoading when {@code true} the constructor skips the loading of the default configuration
	 */
	public TelestionLoader(boolean skipDefaultConfigLoading) {
		if (skipDefaultConfigLoading) return;
		var configType = getConfigType();
		if (Objects.isNull(configType)) return;

		try {
			var defaultConfig = configType.getConstructor().newInstance();
			setDefaultConfig(defaultConfig);
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			logger.info("No default configuration found for {}. " +
							"Expected constructor with no arguments to exist on {}. " +
							"Continuing without default configuration.",
					getClass().getSimpleName(), configType.getSimpleName());
		}
	}

	/**
	 * Same as {@link TelestionLoader#TelestionLoader(boolean)} but enables loading of default configuration
	 * if possible.
	 */
	public TelestionLoader() {
		this(false);
	}

	@Override
	public void setDefaultConfig(JsonObject defaultConfig) {
		// update default configuration
		this.genericDefaultConfig = defaultConfig;
		this.defaultConfig = mapToConfiguration(this.genericDefaultConfig);
		// update configuration
		mergeInConfiguration(this.genericConfig);
	}

	@Override
	public void setDefaultConfig(T defaultConfig) {
		// update default configuration
		this.defaultConfig = defaultConfig;
		this.genericDefaultConfig = defaultConfig.json();
		// update configuration
		mergeInConfiguration(this.genericConfig);
	}

	@Override
	public void setConfig(JsonObject config) {
		// update configuration
		mergeInConfiguration(config);
	}

	@Override
	public void setConfig(T config) {
		// update configuration
		mergeInConfiguration(config.json());
	}

	@Override
	public JsonObject getGenericDefaultConfig() {
		return genericDefaultConfig;
	}

	@Override
	public T getDefaultConfig() {
		return defaultConfig;
	}

	@Override
	public JsonObject getGenericConfig() {
		return genericConfig;
	}

	@Override
	public T getConfig() {
		return config;
	}

	@Override
	public ConfigDeploymentLauncher<? extends JsonObject> getLauncher() {
		return launcher;
	}

	@Override
	public void init(ConfigDeploymentLauncher<? extends JsonObject> launcher) {
		this.launcher = launcher;
	}

	///
	/// Events
	///

	@Override
	public void onInit(Promise<Void> startPromise) throws Exception {
		onInit();
		startPromise.complete();
	}

	/**
	 * Synchronous version of {@link #onInit(Promise)}.
	 */
	public void onInit() throws Exception {
	}

	@Override
	public void onBeforeVertxStartup(Promise<Void> startPromise) throws Exception {
		onBeforeVertxStartup();
		startPromise.complete();
	}

	/**
	 * Synchronous version of {@link #onBeforeVertxStartup(Promise)}.
	 */
	public void onBeforeVertxStartup() throws Exception {
	}

	@Override
	public void onAfterVertxStartup(Promise<Void> startPromise) throws Exception {
		onAfterVertxStartup();
		startPromise.complete();
	}

	/**
	 * Synchronous version of {@link #onAfterVertxStartup(Promise)}.
	 */
	public void onAfterVertxStartup() throws Exception {
	}

	@Override
	public void onBeforeVertxShutdown(Promise<Void> stopPromise) throws Exception {
		onBeforeVertxShutdown();
		stopPromise.complete();
	}

	/**
	 * Synchronous version of {@link #onBeforeVertxShutdown(Promise)}.
	 */
	public void onBeforeVertxShutdown() throws Exception {
	}

	@Override
	public void onAfterVertxShutdown(Promise<Void> stopPromise) throws Exception {
		onAfterVertxShutdown();
		stopPromise.complete();
	}

	/**
	 * Synchronous version of {@link #onAfterVertxShutdown(Promise)}.
	 */
	public void onAfterVertxShutdown() throws Exception {
	}

	@Override
	public void onExit(Promise<Void> stopPromise) throws Exception {
		onExit();
		stopPromise.complete();
	}

	/**
	 * Synchronous version of {@link #onExit(Promise)}.
	 */
	public void onExit() throws Exception {
	}

	@Override
	public void onBeforeVerticleDeploy(Promise<Void> completePromise, Deployment deployment) throws Exception {
		onBeforeVerticleDeploy(deployment);
		completePromise.complete();
	}

	/**
	 * Synchronous version of {@link #onBeforeVerticleDeploy(Promise, Deployment)}.
	 */
	public void onBeforeVerticleDeploy(Deployment deployment) throws Exception {
	}

	@Override
	public void onAfterVerticleDeploy(Promise<Void> completePromise, Deployment deployment) throws Exception {
		onAfterVerticleDeploy(deployment);
		completePromise.complete();
	}

	/**
	 * Synchronous version of {@link #onAfterVerticleDeploy(Promise, Deployment)}.
	 */
	public void onAfterVerticleDeploy(Deployment deployment) throws Exception {
	}

	@Override
	public void onBeforeVerticleUndeploy(Promise<Void> completePromise, Deployment deployment) throws Exception {
		onBeforeVerticleUndeploy(deployment);
		completePromise.complete();
	}

	/**
	 * Synchronous version of {@link #onBeforeVerticleUndeploy(Promise, Deployment)}.
	 */
	public void onBeforeVerticleUndeploy(Deployment deployment) throws Exception {
	}

	@Override
	public void onAfterVerticleUndeploy(Promise<Void> completePromise, Deployment deployment) throws Exception {
		onAfterVerticleUndeploy(deployment);
		completePromise.complete();
	}

	/**
	 * Synchronous version of {@link #onAfterVerticleUndeploy(Promise, Deployment)}.
	 */
	public void onAfterVerticleUndeploy(Deployment deployment) throws Exception {
	}

	@Override
	public void onVertxException(Promise<Void> completePromise, Throwable cause) throws Exception {
		onVertxException(cause);
		completePromise.complete();
	}

	/**
	 * Synchronous version of {@link #onVertxException(Promise, Throwable)}.
	 */
	public void onVertxException(Throwable cause) throws Exception {
	}
}
