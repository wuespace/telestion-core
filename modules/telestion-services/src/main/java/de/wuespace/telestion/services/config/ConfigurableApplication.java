package de.wuespace.telestion.services.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigurableApplication extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurableApplication.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
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
				vertx.deployVerticle(v.verticle(), new DeploymentOptions().setConfig(v.jsonConfig()));
			});
		});
	}
}
