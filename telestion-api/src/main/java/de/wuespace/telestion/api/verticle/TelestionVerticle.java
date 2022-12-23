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
	private VerticleConfigStrategy<T> config;

	/**
	 * The default logger instance.
	 */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public final void start(Promise<Void> startPromise) throws Exception {
		var verticleClazz = this.getClass();

		this.config = new VerticleConfigStrategy<T>(config(), getClass());

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
		this.config = new VerticleConfigStrategy<T>(config(), defaultConfig, getClass());
	}

	/**
	 * Set the default verticle configuration and update the verticle configuration.
	 *
	 * @param defaultConfig the new default verticle configuration
	 */
	public void setDefaultConfig(T defaultConfig) {
		this.config = new VerticleConfigStrategy<T>(config(), defaultConfig, getClass());
	}

	/**
	 * @see VerticleConfigStrategy#getDefaultConfig()
	 */
	public T getDefaultConfig() {
		return config.getDefaultConfig();
	}

	/**
	 * @see VerticleConfigStrategy#getUntypedDefaultConfig()
	 */
	public JsonObject getUntypedDefaultConfig() {
		return config.getUntypedDefaultConfig();
	}

	/**
	 * @see VerticleConfigStrategy#getConfig()
	 */
	public T getConfig() {
		return config.getConfig();
	}

	/**
	 * @see VerticleConfigStrategy#getUntypedConfig()
	 */
	public JsonObject getUntypedConfig() {
		return config.getUntypedConfig();
	}

	/**
	 * Block the usage of <code>config()</code> in inheriting classes.
	 *
	 * @return the verticle configuration from vertx merged with the default configuration
	 */
	@Override
	public final JsonObject config() {
		return super.config();
	}
}
