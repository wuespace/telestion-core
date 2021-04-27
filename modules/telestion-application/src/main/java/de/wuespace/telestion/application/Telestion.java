package de.wuespace.telestion.application;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.wuespace.telestion.core.config.Configuration;

/**
 * This is going to be the Telestion application. It launches the verticles which are specified in the configuration.
 *
 * @author Jan von Pichowski
 */
public final class Telestion extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(Telestion.class);

	/**
	 * Deploys this Telestion verticle.
	 *
	 * @param args of the console
	 */
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(Telestion.class.getName());
	}

	@Override
	public void start(Promise<Void> startPromise) {
		ConfigRetriever retriever = ConfigRetriever.create(vertx);
		retriever.getConfig(configRes -> {
			if (configRes.failed()) {
				logger.error("Failed to load config", configRes.cause());
				startPromise.fail(configRes.cause());
				return;
			}
			var conf = configRes.result().getJsonObject("org.telestion.configuration").mapTo(Configuration.class);
			conf.verticles().stream().flatMap(c -> Collections.nCopies(c.magnitude(), c).stream()).forEach(v -> {
				logger.info("Deploying {}", v.name());
				var future = vertx.deployVerticle(v.verticle(), new DeploymentOptions().setConfig(v.jsonConfig()));
				future.onFailure(Throwable::printStackTrace);
			});
			startPromise.complete();
		});
	}

	@Override
	public void stop(Promise<Void> stopPromise) throws Exception {

	}
}
