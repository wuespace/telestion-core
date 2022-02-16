package de.wuespace.telestion.services.loader;

import de.wuespace.telestion.application.deployment.Deployment;
import de.wuespace.telestion.application.loader.GenericLoaderConfiguration;
import de.wuespace.telestion.application.loader.TelestionLoader;

/**
 * <h2>Description</h2>
 * The debug loader logs a debug message on every event call
 * that a {@link de.wuespace.telestion.application.loader.Loader Loader} receives.
 * <p>
 * The debug loader ignores any configuration.
 *
 * <h2>Usage</h2>
 * To include it in your {@code telestion.json}, append the classname to the loaders section:
 * <pre>
 * {@code
 * {
 *   "loaders": [
 *     "de.wuespace.telestion.services.loader.DebugLoader"
 *   ]
 * }
 * }
 * </pre>
 *
 * @author Ludwig Richter (@fussel178)
 */
public class DebugLoader extends TelestionLoader<GenericLoaderConfiguration> {

	@Override
	public void onInit() {
		logger.debug("Init event");
	}

	@Override
	public void onBeforeVertxStartup() {
		logger.debug("Before Vertx startup event");
	}

	@Override
	public void onAfterVertxStartup() {
		logger.debug("After Vertx startup event");
	}

	@Override
	public void onBeforeVertxShutdown() {
		logger.debug("Before Vertx shutdown event");
	}

	@Override
	public void onAfterVertxShutdown() {
		logger.debug("After Vertx shutdown event");
	}

	@Override
	public void onExit() {
		logger.debug("Exit event");
	}

	@Override
	public void onBeforeVerticleDeploy(Deployment deployment) {
		logger.debug("Before Verticle deploy event");
		logger.debug("Deployment: {}", deployment);
	}

	@Override
	public void onAfterVerticleDeploy(Deployment deployment) {
		logger.debug("After Verticle deploy event");
		logger.debug("Deployment: {}", deployment);
	}

	@Override
	public void onBeforeVerticleUndeploy(Deployment deployment) {
		logger.debug("Before Verticle undeploy event");
		logger.debug("Deployment: {}", deployment);
	}

	@Override
	public void onAfterVerticleUndeploy(Deployment deployment) {
		logger.debug("After Verticle undeploy event");
		logger.debug("Deployment: {}", deployment);
	}

	@Override
	public void onVertxException(Throwable cause) {
		logger.error("Vertx exception event");
		logger.error("Cause:", cause);
	}
}
