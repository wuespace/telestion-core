package de.wuespace.telestion.application.launcher;

import de.wuespace.telestion.application.deployment.Deployment;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;

import java.util.List;

/**
 * An extended version of the {@link LoaderLauncher}.
 * <p>
 * It provides support for deploying {@link Verticle Verticles} onto the {@link io.vertx.core.Vertx Vertx} instance
 * of the {@link VertxLauncher}. A {@link Deployment} is used to represent the running
 * {@link Verticle verticle} on the Vertx instance.
 *
 * @author Ludwig Richter (@fussel178)
 * @see LoaderLauncher
 * @see Deployment
 */
public interface DeploymentLauncher extends LoaderLauncher {

	/**
	 * Add a deployment to the deployment list of the launcher instance.
	 *
	 * @param deployment the deployment that you want to add
	 * @return the added deployment for further usage
	 */
	Deployment addDeployment(Deployment deployment);

	/**
	 * Get the current deployments on this launcher instance.
	 *
	 * @return the current deployments
	 */
	List<Deployment> getDeployments();
}
