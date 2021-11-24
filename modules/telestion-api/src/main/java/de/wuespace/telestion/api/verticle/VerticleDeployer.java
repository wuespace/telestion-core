package de.wuespace.telestion.api.verticle;

import de.wuespace.telestion.api.message.JsonMessage;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * The verticle deployer is a support class to conveniently deploy verticles
 * with or without configuration mainly for testing purposes.
 * The configuration can be generic or typed via the {@link JsonMessage} interface.
 *
 * @author Ludwig Richter
 */
public class VerticleDeployer {
	/**
	 * @see Vertx#deployVerticle(Verticle, DeploymentOptions)
	 * @param configuration the configuration the verticle receives
	 */
	public static Future<String> deploy(Vertx vertx, Verticle verticle, JsonObject configuration) {
		return vertx.deployVerticle(verticle, new DeploymentOptions().setConfig(configuration));
	}

	/**
	 * @see Vertx#deployVerticle(Verticle, DeploymentOptions)
	 * @param configuration the configuration the verticle receives
	 */
	public static Future<String> deploy(Vertx vertx, Verticle verticle, JsonMessage configuration) {
		return deploy(vertx, verticle, configuration.json());
	}

	/**
	 * @see Vertx#deployVerticle(Verticle)
	 */
	public static Future<String> deploy(Vertx vertx, Verticle verticle) {
		return vertx.deployVerticle(verticle);
	}

	/**
	 * @see Vertx#deployVerticle(String, DeploymentOptions)
	 * @param configuration the configuration the verticle receives
	 */
	public static Future<String> deploy(Vertx vertx, String className, JsonObject configuration) {
		return vertx.deployVerticle(className, new DeploymentOptions().setConfig(configuration));
	}

	/**
	 * @see Vertx#deployVerticle(String, DeploymentOptions)
	 * @param configuration the configuration the verticle receives
	 */
	public static Future<String> deploy(Vertx vertx, String className, JsonMessage configuration) {
		return deploy(vertx, className, configuration.json());
	}

	/**
	 * @see Vertx#deployVerticle(String)
	 */
	public static Future<String> deploy(Vertx vertx, String className) {
		return vertx.deployVerticle(className);
	}

	/**
	 * @see Vertx#deployVerticle(Class, DeploymentOptions)
	 * @param configuration the configuration the verticle receives
	 */
	public static Future<String> deploy(Vertx vertx, Class<? extends Verticle> verticleClass, JsonObject configuration) {
		return vertx.deployVerticle(verticleClass, new DeploymentOptions().setConfig(configuration));
	}

	/**
	 * @see Vertx#deployVerticle(Class, DeploymentOptions)
	 * @param configuration the configuration the verticle receives
	 */
	public static Future<String> deploy(Vertx vertx, Class<? extends Verticle> verticleClass, JsonMessage configuration) {
		return deploy(vertx, verticleClass, configuration.json());
	}
}
