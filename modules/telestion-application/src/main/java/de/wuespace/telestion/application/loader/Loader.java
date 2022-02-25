package de.wuespace.telestion.application.loader;

import de.wuespace.telestion.api.DefaultConfigurable;
import de.wuespace.telestion.application.deployment.Deployment;
import de.wuespace.telestion.application.launcher.ConfigDeploymentLauncher;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A loader is a collection of event handlers that hook into different processes of the associated
 * {@link ConfigDeploymentLauncher Launcher}.
 * <p>
 * It can register event handlers in:
 * <ul>
 *     <li>the startup and shutdown process of the loader</li>
 *     <li>the {@link io.vertx.core.Verticle Verticle} deployment and un-deployment</li>
 *     <li>the Vertx exception handling</li>
 * </ul>
 * <p>
 * To start, take a look at the {@link TelestionLoader} as an abstract reference implementation.
 *
 * @param <T> the type of the loader configuration
 * @author Ludwig Richter (@fussel178)
 * @see TelestionLoader
 * @see LoaderConfiguration
 * @see ConfigDeploymentLauncher
 */
public interface Loader<T extends LoaderConfiguration> extends DefaultConfigurable<T> {

	/**
	 * Instantiates a new loader from a given classname.
	 *
	 * @param className the classname or binary name of the loader class
	 * @return the loader instance associated to the classname
	 */
	static Loader<?> instantiate(String className)
			throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, ClassCastException {

		try {
			var classLoader = Loader.class.getClassLoader();
			var loaderClass = classLoader.loadClass(className);
			return (Loader<?>) loaderClass.getConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException(("Cannot find loader with classname %s. Please check the given " +
					"classname, install required packages and try again.").formatted(className), e.getException());
		} catch (InvocationTargetException e) {
			throw new InvocationTargetException(e.getTargetException(), ("The loader %s cannot be created because " +
					"the constructor throws an exception during instantiation. Please check your loader's " +
					"constructor, fix the breaking code and try again.").formatted(className));
		} catch (InstantiationException e) {
			throw new InstantiationException(("Abstract loaders aren't supported. Please extend the \"Loader\" " +
					"class in loader %s, add the sub class to the configuration and try again.").formatted(className));
		} catch (IllegalAccessException e) {
			throw new IllegalAccessException(("The class loader cannot instantiate the loader %s because " +
					"the constructor is inaccessible. Please add a public constructor with no arguments " +
					"to continue.").formatted(className));
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodException("The loader %s does not contain a constructor. Please add one to continue."
					.formatted(className));
		} catch (ClassCastException e) {
			throw new ClassCastException(("The given class behind the classname %s isn't a loader. Please implement " +
					"the \"Loader\" interface in your loader and try again.").formatted(className));
		}
	}

	/**
	 * Calls a selected event handler on a list of loaders.
	 * <p>
	 * The selector receives the loader and a promise for the event handler of the loader.
	 * It "specifies" the event handler that should be called.
	 *
	 * <h4>Usage</h4>
	 * <pre>
	 * {@code
	 * Loader.call(loaders, Loader::onBeforeVertxStartup)
	 * 	.onSuccess(result -> logger.info("Loaders called"));
	 * }
	 * </pre>
	 *
	 * @param loaders  the list of loaders on which the event handler should be called
	 * @param selector the selector which selects the event handler that should be called
	 * @return a future which resolves when all loaders resolve
	 */
	static Future<CompositeFuture> call(List<Loader<?>> loaders, EventSelector selector) {
		return CompositeFuture.join(loaders.stream().map(loader -> Future.<Void>future(promise -> {
			try {
				selector.handle(loader, promise);
			} catch (Exception e) {
				promise.tryFail(e);
			}
		})).collect(Collectors.toList()));
	}

	/**
	 * Returns a reference to the {@link ConfigDeploymentLauncher Launcher} instance which deployed this loader.
	 *
	 * @return the associated {@link ConfigDeploymentLauncher Launcher} instance
	 */
	ConfigDeploymentLauncher<? extends JsonObject> getLauncher();

	/**
	 * Set the current loader configuration.
	 *
	 * @param config the loader configuration
	 */
	void setConfig(JsonObject config);

	/**
	 * Set the current loader configuration.
	 *
	 * @param config the loader configuration
	 */
	void setConfig(T config);

	/**
	 * Initializes the loader with the associated {@link ConfigDeploymentLauncher Launcher} instance
	 * which deployed this loader.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 *
	 * @param launcher the Telestion Launcher which deployed this loader
	 */
	void init(ConfigDeploymentLauncher<? extends JsonObject> launcher);

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} initializes itself, but before it parses
	 * the {@link io.vertx.core.VertxOptions VertxOptions} from the main configuration.
	 * <p>
	 * Use this event if you want to run initialization steps that don't depend on Vertx options or an instance.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * <strong><code>onInit</code></strong> ➔
	 * {@link #onBeforeVertxStartup(Promise) onBeforeVertxStartup} ➔
	 * {@link #onAfterVertxStartup(Promise) onAfterVertxStartup}
	 *
	 * @param startPromise the promise that indicates the completion state of this loader
	 */
	void onInit(Promise<Void> startPromise) throws Exception;

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} parses the
	 * {@link io.vertx.core.VertxOptions VertxOptions}, but before it creates the
	 * {@link io.vertx.core.Vertx Vertx} instance.
	 * <p>
	 * Use this event if you want to run startup steps that depend on the Vertx options but not on a running instance.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * {@link #onInit(Promise) onInit} ➔
	 * <strong><code>onBeforeVertxStartup</code></strong> ➔
	 * {@link #onAfterVertxStartup(Promise) onAfterVertxStartup}
	 *
	 * @param startPromise the promise that indicates the completion state of this loader
	 */
	void onBeforeVertxStartup(Promise<Void> startPromise) throws Exception;

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} creates a
	 * {@link io.vertx.core.Vertx Vertx} instance and is ready to complete the startup process.
	 * <p>
	 * Use this event if you want to run startup steps that depend on a running Vertx instance.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * {@link #onInit(Promise) onInit} ➔
	 * {@link #onBeforeVertxStartup(Promise) onBeforeVertxStartup} ➔
	 * <strong><code>onAfterVertxStartup</code></strong>
	 *
	 * @param startPromise the promise that indicates the completion state of this loader
	 */
	void onAfterVertxStartup(Promise<Void> startPromise) throws Exception;

	/**
	 * Gets called before the {@link ConfigDeploymentLauncher Launcher} closes the running
	 * {@link io.vertx.core.Vertx Vertx} instance.
	 * <p>
	 * Use this event if you want to run shutdown steps that depend on a running Vertx instance.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * <strong><code>onBeforeVertxShutdown</code></strong> ➔
	 * {@link #onAfterVertxShutdown(Promise) onAfterVertxShutdown} ➔
	 * {@link #onExit(Promise) onExit}
	 *
	 * @param stopPromise the promise that indicates the completion state of this loader
	 */
	void onBeforeVertxShutdown(Promise<Void> stopPromise) throws Exception;

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} closes the
	 * {@link io.vertx.core.Vertx Vertx} instance, but before it removes the instance and the related
	 * {@link io.vertx.core.VertxOptions VertxOptions}.
	 * <p>
	 * Use this event if you want to run shutdown steps that don't depend on a running Vertx instance but you want
	 * it to run before the exit step.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * {@link #onBeforeVertxShutdown(Promise) onBeforeVertxShutdown} ➔
	 * <strong><code>onAfterVertxShutdown</code></strong> ➔
	 * {@link #onExit(Promise) onExit}
	 *
	 * @param stopPromise the promise that indicates the completion state of this loader
	 */
	void onAfterVertxShutdown(Promise<Void> stopPromise) throws Exception;

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} removes the {@link io.vertx.core.Vertx Vertx}
	 * instance and {@link io.vertx.core.VertxOptions VertxOptions} and is ready to complete the shutdown process.
	 * <p>
	 * Use this event if you want to run shutdown steps that should run directly before the launcher stops.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * {@link #onBeforeVertxShutdown(Promise) onBeforeVertxShutdown} ➔
	 * {@link #onAfterVertxShutdown(Promise) onAfterVertxShutdown} ➔
	 * <strong><code>onExit</code></strong>
	 *
	 * @param stopPromise the promise that indicates the completion state of this loader
	 */
	void onExit(Promise<Void> stopPromise) throws Exception;

	/**
	 * Gets called before the {@link ConfigDeploymentLauncher Launcher} deploys a new
	 * {@link io.vertx.core.Verticle Verticle}.
	 * <p>
	 * Use this event if you want to run steps before a verticle deploys.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution. A failed promise <strong>only prevents</strong> this specific deployment
	 * from deploying.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * <strong><code>onBeforeVerticleDeploy</code></strong> ➔
	 * {@link #onAfterVerticleDeploy(Promise, Deployment) onAfterVerticleDeploy}
	 *
	 * @param completePromise the promise that indicates the completion state of this loader
	 * @param deployment      the deployment which contains the {@link io.vertx.core.Verticle Verticle} that deploys
	 */
	void onBeforeVerticleDeploy(Promise<Void> completePromise, Deployment deployment) throws Exception;

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} deploys a new
	 * {@link io.vertx.core.Verticle Verticle}.
	 * <p>
	 * Use this event if you want to run steps after a verticle deploys.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * {@link #onBeforeVerticleDeploy(Promise, Deployment) onBeforeVerticleDeploy} ➔
	 * <strong><code>onAfterVerticleDeploy</code></strong>
	 *
	 * @param completePromise the promise that indicates the completion state of this loader
	 * @param deployment      the deployment which contains the {@link io.vertx.core.Verticle Verticle} that deploys
	 */
	void onAfterVerticleDeploy(Promise<Void> completePromise, Deployment deployment) throws Exception;

	/**
	 * Gets called before the {@link ConfigDeploymentLauncher Launcher} un-deploys a running
	 * {@link io.vertx.core.Verticle Verticle}.
	 * <p>
	 * Use this event if you want to run steps before a verticle un-deploys.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution. A failed promise <strong>only prevents</strong> this specific deployment
	 * from un-deploying.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * <strong><code>onBeforeVerticleUndeploy</code></strong> ➔
	 * {@link #onAfterVerticleUndeploy(Promise, Deployment) onAfterVerticleUndeploy}
	 *
	 * @param completePromise the promise that indicates the completion state of this loader
	 * @param deployment      the deployment which contains the {@link io.vertx.core.Verticle Verticle} that un-deploys
	 */
	void onBeforeVerticleUndeploy(Promise<Void> completePromise, Deployment deployment) throws Exception;

	/**
	 * Gets called after the {@link ConfigDeploymentLauncher Launcher} un-deploys a running
	 * {@link io.vertx.core.Verticle Verticle}.
	 * <p>
	 * Use this event if you want to run steps after a verticle un-deploys.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 * <p>
	 * <em>Call chain</em>:
	 * {@link #onBeforeVerticleUndeploy(Promise, Deployment) onBeforeVerticleUndeploy} ➔
	 * <strong><code>onAfterVerticleUndeploy</code></strong>
	 *
	 * @param completePromise the promise that indicates the completion state of this loader
	 * @param deployment      the deployment which contains the {@link io.vertx.core.Verticle Verticle} that un-deploys
	 */
	void onAfterVerticleUndeploy(Promise<Void> completePromise, Deployment deployment) throws Exception;

	/**
	 * Gets called when the running {@link io.vertx.core.Vertx Vertx} instance in the
	 * {@link ConfigDeploymentLauncher Launcher} encounters an uncaught exception.
	 * <p>
	 * Use this event if you want to run steps on uncaught exception on the Vertx instance.
	 * <p>
	 * The passed promise should resolve when this loader finishes its execution and fail if the loader encounters
	 * an error during execution.
	 * <p>
	 * The launcher calls this event handler. <strong>Don't call it by yourself.</strong>
	 *
	 * @param completePromise the promise that indicates the completion state of this loader
	 * @param cause           the cause that the Vertx instances encounters
	 */
	void onVertxException(Promise<Void> completePromise, Throwable cause) throws Exception;
}
