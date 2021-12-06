package de.wuespace.telestion.deployment;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This class represents an element in the deployment list of the VerticleDeployer.
 * It contains the required information to deploy and undeploy a specific verticles
 * to and from a {@link Vertx} instance with or without configuration.
 * <p>
 * Due to Vert.x design, a verticle can be described as:
 * <ul>
 *     <li>an instance of the Verticle class</li>
 *     <li>a class type of the verticle</li>
 *     <li>or the class name string of the verticle class</li>
 * </ul>
 * <p>
 * Only one type of the above is valid as a reference to the Verticle that should be deployed.
 *
 * @author Ludwig Richter
 */
public class VerticleDeploymentElement extends VerticleDeploymentElementPrototype {
	protected final Verticle verticle;

	protected String deploymentId;

	public static VerticleDeploymentElement createFrom(VerticleDeploymentElementPrototype prototype) {
		// clone all attributes
		// JsonObject is mutable -> copy to create own instance
		return new VerticleDeploymentElement(null, prototype.verticleClass, prototype.verticleClassName, prototype.configuration.copy());
	}

	/**
	 * Create a new deployment element with a verticle instance as reference and an optional configuration.
	 * <p>
	 * <strong>Note:</strong>
	 * All other references are {@code null} in this case.
	 * </p>
	 *
	 * @param verticle      the instance of a Verticle class
	 * @param configuration the optional configuration the verticle receives at start
	 */
	public VerticleDeploymentElement(Verticle verticle, JsonObject configuration) {
		this(verticle, null, null, configuration);
	}

	/**
	 * Create a new deployment element with a class type of the Verticle as reference
	 * and an optional configuration.
	 * <p>
	 * <strong>Note:</strong>
	 * All other references are {@code null} in this case.
	 * </p>
	 *
	 * @param verticleClass the class type of the Verticle
	 * @param configuration the optional configuration the verticle receives at start
	 */
	public VerticleDeploymentElement(Class<? extends Verticle> verticleClass, JsonObject configuration) {
		this(null, verticleClass, null, configuration);
	}

	/**
	 * Create a new deployment element with the class name of the Verticle as reference
	 * and an optional configuration.
	 * <p>
	 * <strong>Note:</strong>
	 * All other references are {@code null} in this case.
	 * </p>
	 *
	 * @param verticleClassName the class name of the Verticle
	 * @param configuration     the optional configuration the verticle receives at start
	 */
	public VerticleDeploymentElement(String verticleClassName, JsonObject configuration) {
		this(null, null, verticleClassName, configuration);
	}

	protected VerticleDeploymentElement(Verticle verticle, Class<? extends Verticle> verticleClass, String verticleClassName, JsonObject configuration) {
		super(verticleClass, verticleClassName, configuration);
		this.verticle = verticle;
	}

	/**
	 * Get the Verticle instance <strong>if</strong> the deployment element was created with this reference.
	 * Otherwise, it is {@code null}.
	 *
	 * @return the Verticle instance as reference
	 */
	public Verticle getVerticle() {
		return this.verticle;
	}

	public String getDeploymentId() {
		return this.deploymentId;
	}

	public Future<String> deployOn(Vertx vertx) {
		var options = new DeploymentOptions();

		if (configuration != null) {
			options.setConfig(configuration);
		}

		Future<String> deployment;
		if (verticle != null) {
			deployment = vertx.deployVerticle(verticle, options);
		} else if (verticleClass != null) {
			deployment = vertx.deployVerticle(verticleClass, options);
		} else if (verticleClassName != null) {
			deployment = vertx.deployVerticle(verticleClassName, options);
		} else {
			throw new IllegalArgumentException("No verticle information given. Cannot deploy verticle prototype");
		}

		return deployment.onSuccess(deploymentId -> this.deploymentId = deploymentId);
	}

	public Future<Void> undeployOn(Vertx vertx) {
		if (!isDeployed()) {
			throw new RuntimeException("Verticle is not deployed yet");
		}

		return vertx.undeploy(this.deploymentId).onSuccess(arg -> this.deploymentId = null);
	}

	public boolean isDeployed() {
		return this.deploymentId != null;
	}
}
