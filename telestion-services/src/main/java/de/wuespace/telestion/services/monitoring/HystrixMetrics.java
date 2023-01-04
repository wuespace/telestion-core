package de.wuespace.telestion.services.monitoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.verticle.TelestionConfiguration;
import de.wuespace.telestion.api.verticle.TelestionVerticle;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.circuitbreaker.HystrixMetricHandler;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

/**
 * A verticle which streams the hystrix-metrics to a given address. You could view it using the hystrix-dashboard
 * (<a href="https://github.com/kennedyoliveira/standalone-hystrix-dashboard">...</a>)
 *
 * @author Jan von Pichowski (@jvpichovski), Ludwig Richter (@fussel178)
 */
public class HystrixMetrics extends TelestionVerticle<HystrixMetrics.Configuration> {

	public record Configuration(
			@JsonProperty String host,
			@JsonProperty int port,
			@JsonProperty String path,
			@JsonProperty String circuitBreakerAddress
	) implements TelestionConfiguration {
		public Configuration() {
			this("0.0.0.0", 8080, "/hystrix-metrics", CircuitBreakerOptions.DEFAULT_NOTIFICATION_ADDRESS);
		}
	}

	@Override
	public void onStart(Promise<Void> startPromise) throws Exception {
		// register circuit breaker handler in sub-route
		var router = Router.router(vertx);
		var circuitBreakerHandler = HystrixMetricHandler.create(vertx, getConfig().circuitBreakerAddress());
		router.get(getConfig().path()).handler(circuitBreakerHandler);

		// create and start http server
		var server = vertx.createHttpServer();
		server.requestHandler(router).listen(getConfig().port(), getConfig().host())
				.onSuccess(s -> logger.info("HTTP server listening on {}:{}", getConfig().host(), getConfig().port()))
				.onSuccess(s -> startPromise.complete())
				.onFailure(startPromise::fail);
	}
}
