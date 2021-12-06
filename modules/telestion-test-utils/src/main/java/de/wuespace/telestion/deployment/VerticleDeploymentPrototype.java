package de.wuespace.telestion.deployment;

import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class VerticleDeploymentPrototype {
	private final List<VerticleDeploymentElementPrototype> list;

	public VerticleDeploymentPrototype() {
		this.list = new ArrayList<>();
	}

	public List<VerticleDeploymentElementPrototype> getList() {
		return this.list;
	}

	/**
	 * Add a verticle class with generic configuration to the deployment list.
	 * @param verticleClass the class type of the verticle that should be deployed later
	 * @param configuration the generic configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeploymentPrototype add(Class<? extends Verticle> verticleClass, JsonObject configuration) {
		list.add(new VerticleDeploymentElementPrototype(verticleClass, configuration));
		return this;
	}

	/**
	 * Add a verticle class with Telestion configuration to the deployment list.
	 * @param verticleClass the class type of the verticle that should be deployed later
	 * @param configuration the Telestion configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeploymentPrototype add(Class<? extends Verticle> verticleClass, TelestionConfiguration configuration) {
		return add(verticleClass, configuration.json());
	}

	/**
	 * Add a verticle class without configuration to the deployment list.
	 * @param verticleClass the class type of the verticle that should be deployed later
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeploymentPrototype add(Class<? extends Verticle> verticleClass) {
		return add(verticleClass, (JsonObject) null);
	}

	/**
	 * Add a verticle class name string with generic configuration to the deployment list.
	 * @param verticleClassName the class name of the verticle that should be deployed later
	 * @param configuration the generic configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeploymentPrototype add(String verticleClassName, JsonObject configuration) {
		list.add(new VerticleDeploymentElementPrototype(verticleClassName, configuration));
		return this;
	}

	/**
	 * Add a verticle class name string with Telestion configuration to the deployment list.
	 * @param verticleClassName the class name of the verticle that should be deployed later
	 * @param configuration the Telestion configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeploymentPrototype add(String verticleClassName, TelestionConfiguration configuration) {
		return add(verticleClassName, configuration.json());
	}

	/**
	 * Add a verticle class name string without configuration to the deployment list.
	 * @param verticleClassName the class name of the verticle that should be deployed later
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeploymentPrototype add(String verticleClassName) {
		return add(verticleClassName, (JsonObject) null);
	}

	public VerticleDeployment create(Vertx vertx) {
		return VerticleDeployment.createFrom(vertx, this);
	}
}
