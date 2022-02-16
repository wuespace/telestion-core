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


	/**
	 * Like {@link #deploy(Class)} but with custom {@link DeploymentOptions deployment options}.
	 * These {@link DeploymentOptions deployment options} are passed to the {@link io.vertx.core.Vertx Vert.x instance}
	 * on deployment.
	 *
	 * @param verticleClass the class type of the {@link Verticle verticle} that you want deploy
	 * @param options       custom {@link DeploymentOptions deployment options} for your running verticle
	 * @return a future which resolves with a {@link Deployment} which represents your {@link Verticle verticle}
	 * when it is successfully deployed
	 * @see io.vertx.core.Vertx#deployVerticle(Class, DeploymentOptions)
	 */
	default Future<Deployment> deploy(Class<? extends Verticle> verticleClass, DeploymentOptions options) {
		return addDeployment(new Deployment(this, verticleClass, options)).deploy();
	}

	/**
	 * Like {@link #deploy(Verticle)} but the {@link Verticle verticle} instance is created
	 * by invoking the default constructor.
	 *
	 * @param verticleClass the class type of the {@link Verticle verticle} that you want deploy
	 * @return a future which resolves with a {@link Deployment} which represents your {@link Verticle verticle}
	 * when it is successfully deployed
	 * @see io.vertx.core.Vertx#deployVerticle(Class, DeploymentOptions)
	 */
	default Future<Deployment> deploy(Class<? extends Verticle> verticleClass) {
		return addDeployment(new Deployment(this, verticleClass)).deploy();
	}

	/**
	 * Like {@link #deploy(Verticle)} but with custom {@link DeploymentOptions}.
	 * The deployment options configure the {@link Verticle} during deployment.
	 *
	 * @param verticle your created verticle that you want to deploy
	 * @param options  deployment options for your running verticle
	 * @return a future which resolves with a {@link Deployment} which represents your verticle
	 * when it is successfully deployed
	 * @see io.vertx.core.Vertx#deployVerticle(Verticle, DeploymentOptions)
	 */
	default Future<Deployment> deploy(Verticle verticle, DeploymentOptions options) {
		return addDeployment(new Deployment(this, verticle, options)).deploy();
	}

	/**
	 * Deploys a {@link Verticle} on the {@link io.vertx.core.Vertx Vertx} instance
	 * of the {@link VertxLauncher} that you have created by yourself.
	 *
	 * @param verticle your created verticle that you want to deploy
	 * @return a future which resolves with a {@link Deployment} which represents your verticle
	 * when it successfully deploys
	 * @see io.vertx.core.Vertx#deployVerticle(Verticle)
	 */
	default Future<Deployment> deploy(Verticle verticle) {
		return addDeployment(new Deployment(this, verticle)).deploy();
	}

	/**
	 * Like {@link #deploy(String)} but with custom {@link DeploymentOptions deployment options}.
	 * These {@link DeploymentOptions deployment options} are passed to the {@link io.vertx.core.Vertx Vert.x instance}
	 * on deployment.
	 *
	 * @param className the classname or binary name of your {@link Verticle verticle} class
	 * @param options   custom {@link DeploymentOptions deployment options} for your running verticle
	 * @return a future which resolves with a {@link Deployment} which represents your {@link Verticle verticle}
	 * when it is successfully deployed
	 * @see io.vertx.core.Vertx#deployVerticle(String, DeploymentOptions)
	 */
	default Future<Deployment> deploy(String className, DeploymentOptions options) {
		return addDeployment(new Deployment(this, className, options)).deploy();
	}

	/**
	 * Like {@link #deploy(Verticle)} but the {@link Verticle verticle} instance is created
	 * by a {@link io.vertx.core.spi.VerticleFactory verticle factory} based on the classname.
	 *
	 * @param className the classname or binary name of your {@link Verticle verticle} class
	 * @return a future which resolves with a {@link Deployment} which represents your {@link Verticle verticle}
	 * when it is successfully deployed
	 * @see io.vertx.core.Vertx#deployVerticle(String)
	 */
	default Future<Deployment> deploy(String className) {
		return addDeployment(new Deployment(this, className)).deploy();
	}
}
