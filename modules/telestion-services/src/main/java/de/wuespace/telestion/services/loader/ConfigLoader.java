package de.wuespace.telestion.services.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.services.loader.verticle.AbstractVerticleLoader;
import de.wuespace.telestion.services.loader.verticle.VerticleLoaderConfiguration;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.Promise;

/**
 * <h2>Description</h2>
 * The {@link ConfigLoader} retrieves a configuration from different stores using the
 * <a href="https://vertx.io/docs/vertx-config/java/">Vert.x Config</a> module.
 * <p>
 * The loader reads {@link de.wuespace.telestion.services.loader.verticle.VerticleConfiguration VerticleConfigurations}
 * from the main configuration and deploys them on the associated launcher.
 * <p>
 * This configuration specifies verticle classnames and configurations
 * which should be deployed on the associated launcher instance.
 * <p>
 * <strong>Note:</strong> If you want to deploy verticles from the main configuration,
 * please use the {@link VerticleLoader} instead.
 *
 * <h2>Usage</h2>
 * Append the classname of the {@link ConfigLoader} to the {@code telestion.json} and configure it
 * according to the {@link ConfigRetrieverOptions}.
 * <p>
 * To include the default store which retrieves the configuration from the file {@code conf/config.json}, add:
 * <pre>
 * {@code
 * {
 *   "loaders": [
 *     {
 *       "className": "de.telestion.wuespace.services.loader.ConfigLoader",
 *       "config": {
 *         "includeDefaultStores": true,
 *         "stores": []
 *       }
 *     }
 *   ]
 * }
 * }
 * </pre>
 *
 * @author Ludwig Richter (@fussel178)
 */
public class ConfigLoader extends AbstractVerticleLoader<ConfigLoader.Configuration> {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Configuration(
			@JsonProperty String verticlesProperty
	) implements VerticleLoaderConfiguration {
	}

	@Override
	public void onAfterVertxStartup(Promise<Void> startPromise) {
		logger.info("Deploy verticles from configuration");

		var options = new ConfigRetrieverOptions(getGenericConfig());
		logger.debug("Configuration retriever options: {}", options);

		ConfigRetriever.create(launcher.getVertx(), options).getConfig()
				.compose(this::onConfiguration)
				.onComplete(startPromise);
	}
}
