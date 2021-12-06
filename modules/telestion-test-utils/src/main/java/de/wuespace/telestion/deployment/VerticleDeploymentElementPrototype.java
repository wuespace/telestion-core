package de.wuespace.telestion.deployment;

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
public class VerticleDeploymentElementPrototype {
	protected final Class<? extends Verticle> verticleClass;

	protected final String verticleClassName;

	protected final JsonObject configuration;

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
	public VerticleDeploymentElementPrototype(Class<? extends Verticle> verticleClass, JsonObject configuration) {
		this(verticleClass, null, configuration);
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
	public VerticleDeploymentElementPrototype(String verticleClassName, JsonObject configuration) {
		this(null, verticleClassName, configuration);
	}

	protected VerticleDeploymentElementPrototype(Class<? extends Verticle> verticleClass, String verticleClassName, JsonObject configuration) {
		this.verticleClass = verticleClass;
		this.verticleClassName = verticleClassName;
		this.configuration = configuration;
	}

	/**
	 * Get the class type of the Verticle <strong>if</strong> the deployment element was created with this reference.
	 * Otherwise, it is {@code null}.
	 *
	 * @return the class type of the Verticle
	 */
	public Class<? extends Verticle> getVerticleClass() {
		return this.verticleClass;
	}

	/**
	 * Get the class name of the Verticle <strong>if</strong> the deployment element was created with this reference.
	 * Otherwise, it is {@code null}.
	 *
	 * @return the class name of the Verticle
	 */
	public String getVerticleClassName() {
		return this.verticleClassName;
	}

	/**
	 * Get the configuration of the deployment.
	 *
	 * @return the configuration of
	 */
	public JsonObject getConfiguration() {
		return this.configuration;
	}

	public VerticleDeploymentElement create() {
		return VerticleDeploymentElement.createFrom(this);
	}
}
