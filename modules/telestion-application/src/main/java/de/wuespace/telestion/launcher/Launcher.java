package de.wuespace.telestion.launcher;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.time.Duration;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic org.telestion.launcher.launcher class which deploys {@link Verticle Verticles}.
 *
 * @author Jan von Pichowski, Cedric Boes
 * @version 1.0
 * @see Verticle
 */
public final class Launcher {

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	/**
	 * Simply calls {@link #start(String...)}.
	 *
	 * @param args the class names of the {@link Verticle Verticles} which should be deployed
	 */
	public static void main(String[] args) {
		start(args);
	}

	/**
	 * Deploys the given {@link Verticle Verticles}.<br>
	 * If Vert.x fails to deploy a {@link Verticle}, it will retry after 5 secs.
	 *
	 * @param verticleNames the class names of the {@link Verticle Verticles} which should be deployed
	 */
	public static void start(String... verticleNames) {
		logger.info("Deploying {} verticles", verticleNames.length);
		var vertx = Vertx.vertx();

		Arrays.stream(verticleNames).forEach(name -> {
			logger.info("Deploying verticle {}", name);
			vertx.setPeriodic(
					Duration.ofSeconds(5).toMillis(),
					timerId -> vertx.deployVerticle(name, deploymentHandler(vertx, name, timerId))
			);
		});
	}

	/**
	 * Deploys the given {@link Verticle Verticles}.<br>
	 * If Vert.x fails to deploy a {@link Verticle}, it will retry after 5 secs.
	 *
	 * @param verticles the verticles to be deployed
	 */
	public static void start(Verticle... verticles) {
		logger.info("Deploying {} verticles", verticles.length);
		var vertx = Vertx.vertx();

		Arrays.stream(verticles).forEach(instance -> {
			logger.info("Deploying verticle {}", instance);
			vertx.setPeriodic(
					Duration.ofSeconds(5).toMillis(),
					timerId -> vertx.deployVerticle(
							instance,
							deploymentHandler(vertx, instance.getClass().getName(), timerId)
					)
			);
		});
	}

	private static Handler<AsyncResult<String>> deploymentHandler(Vertx vertx, String verticle, Long timerId) {
		return res -> {
			if (res.failed()) {
				logger.error("Failed to deploy verticle {} retrying in 5s", verticle, res.cause());
				return;
			}
			logger.info("Deployed verticle {} with id {}", verticle, res.result());
			vertx.cancelTimer(timerId);
		};
	}
}
