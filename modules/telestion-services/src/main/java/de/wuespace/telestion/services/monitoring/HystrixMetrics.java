package de.wuespace.telestion.services.monitoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.circuitbreaker.HystrixMetricHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.wuespace.telestion.api.config.Config;

/**
 * A verticle which streams the hystrix-metrics to a given address. You could view it using the hystrix-dashboard
 * (https://github.com/kennedyoliveira/standalone-hystrix-dashboard)
 *
 * @author Jan von Pichowski
 */
public final class HystrixMetrics extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(HystrixMetrics.class);
	private final Configuration forcedConfig;

	public HystrixMetrics() {
		forcedConfig = null;
	}

	public HystrixMetrics(int port, String path) {
		this.forcedConfig = new Configuration(port, path);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		var config = Config.get(forcedConfig, config(), Configuration.class);
		Router router = Router.router(vertx);
		router.get(config.path).handler(HystrixMetricHandler.create(vertx));
		vertx.createHttpServer().requestHandler(router).listen(config.port);
		startPromise.complete();
		logger.info("Started {} with config {}", HystrixMetrics.class.getSimpleName(), config);
	}

	@SuppressWarnings({ "preview", "unused" })
	private static record Configuration(@JsonProperty int port, @JsonProperty String path) {
		private Configuration() {
			this(8080, "/hystrix-metrics");
		}
	}
}
