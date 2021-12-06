package de.wuespace.telestion.deployment;

import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VerticleDeployment {
	private final Vertx vertx;

	private final List<VerticleDeploymentElement> list;

	public static VerticleDeployment createFrom(Vertx vertx, VerticleDeploymentPrototype prototype) {
		// Create deep copy
		var verticles = prototype.getList().stream().map(VerticleDeploymentElement::createFrom).toList();
		return new VerticleDeployment(vertx, verticles);
	}

	public VerticleDeployment(Vertx vertx) {
		this(vertx, new ArrayList<>());
	}

	protected VerticleDeployment(Vertx vertx, List<VerticleDeploymentElement> list) {
		this.vertx = vertx;
		this.list = list;
	}

	public List<VerticleDeploymentElement> getList() {
		return this.list;
	}

	/**
	 * Add a verticle instance with generic configuration to the deployment list.
	 * @param verticle an instance of the verticle that should be deployed later
	 * @param configuration the generic configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(Verticle verticle, JsonObject configuration) {
		list.add(new VerticleDeploymentElement(verticle, configuration));
		return this;
	}

	/**
	 * Add a verticle instance with Telestion configuration to the deployment list.
	 * @param verticle an instance of the verticle that should be deployed later
	 * @param configuration the Telestion configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(Verticle verticle, TelestionConfiguration configuration) {
		return add(verticle, configuration.json());
	}

	/**
	 * Add a verticle without configuration to the deployment list.
	 * @param verticle an instance of the verticle that should be deployed later
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(Verticle verticle) {
		return add(verticle, (JsonObject) null);
	}

	/**
	 * Add a verticle class with generic configuration to the deployment list.
	 * @param verticleClass the class type of the verticle that should be deployed later
	 * @param configuration the generic configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(Class<? extends Verticle> verticleClass, JsonObject configuration) {
		list.add(new VerticleDeploymentElement(verticleClass, configuration));
		return this;
	}

	/**
	 * Add a verticle class with Telestion configuration to the deployment list.
	 * @param verticleClass the class type of the verticle that should be deployed later
	 * @param configuration the Telestion configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(Class<? extends Verticle> verticleClass, TelestionConfiguration configuration) {
		return add(verticleClass, configuration.json());
	}

	/**
	 * Add a verticle class without configuration to the deployment list.
	 * @param verticleClass the class type of the verticle that should be deployed later
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(Class<? extends Verticle> verticleClass) {
		return add(verticleClass, (JsonObject) null);
	}

	/**
	 * Add a verticle class name string with generic configuration to the deployment list.
	 * @param verticleClassName the class name of the verticle that should be deployed later
	 * @param configuration the generic configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(String verticleClassName, JsonObject configuration) {
		list.add(new VerticleDeploymentElement(verticleClassName, configuration));
		return this;
	}

	/**
	 * Add a verticle class name string with Telestion configuration to the deployment list.
	 * @param verticleClassName the class name of the verticle that should be deployed later
	 * @param configuration the Telestion configuration the verticle receives at start
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(String verticleClassName, TelestionConfiguration configuration) {
		return add(verticleClassName, configuration.json());
	}

	/**
	 * Add a verticle class name string without configuration to the deployment list.
	 * @param verticleClassName the class name of the verticle that should be deployed later
	 * @return the current verticle deployer instance for fluent typing
	 */
	public VerticleDeployment add(String verticleClassName) {
		return add(verticleClassName, (JsonObject) null);
	}

	public CompositeFuture deploy() {
		//noinspection rawtypes
		var futures = list.stream()
				.filter(Predicate.not(VerticleDeploymentElement::isDeployed))
				.map(element -> (Future) element.deployOn(vertx))
				.collect(Collectors.toList());

		return CompositeFuture.all(futures);
	}

	public CompositeFuture undeploy() {
		//noinspection rawtypes
		var futures = list.stream()
				.filter(VerticleDeploymentElement::isDeployed)
				.map(element -> (Future) element.undeployOn(vertx))
				.collect(Collectors.toList());

		return CompositeFuture.all(futures);
	}
}
