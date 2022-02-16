package de.wuespace.telestion.services.loader.verticle;

import de.wuespace.telestion.application.deployment.Deployment;
import de.wuespace.telestion.application.loader.TelestionLoader;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This loader extends the functionality of the {@link TelestionLoader} and provides methods to deploy verticles
 * based on {@link VerticleConfiguration VerticleConfigurations}.
 *
 * @param <T> the type of the loader configuration
 * @author Ludwig Richter (@fussel178)
 */
public abstract class AbstractVerticleLoader<T extends VerticleLoaderConfiguration> extends TelestionLoader<T> {

	/**
	 * The default property name which points to the {@link VerticleConfiguration VerticleConfigurations}
	 * in the loader configuration.
	 */
	public static final String DEFAULT_VERTICLES_PROPERTY = "verticles";

	/**
	 * Gets called, when the loader receive verticles configuration.
	 *
	 * @param configuration the retrieved configuration
	 * @return a future which resolves when all verticles are deployed
	 */
	protected Future<Void> onConfiguration(JsonObject configuration) {
		logger.debug("Received configuration: {}", configuration);
		var verticlesProperty = Objects.isNull(getConfig()) || Objects.isNull(getConfig().verticlesProperty())
				? DEFAULT_VERTICLES_PROPERTY
				: getConfig().verticlesProperty();
		logger.debug("Extract verticles from property: {}", verticlesProperty);

		try {
			var stream = configuration.getJsonArray(verticlesProperty, new JsonArray()).stream()
					// try to cast to JSON object
					.map(object -> (JsonObject) object)
					// try to convert to verticle configuration
					.map(VerticleConfiguration::from)
					// duplicate entries by magnitude
					.flatMap(verticleConfig -> Collections.nCopies(verticleConfig.magnitude(), verticleConfig).stream());

			return deployAll(stream).compose(result -> Future.succeededFuture());
		} catch (Exception e) {
			return Future.failedFuture(e);
		}
	}

	/**
	 * Deploys a list of {@link VerticleConfiguration VerticleConfigurations}.
	 *
	 * @param list the list that contains verticle configurations
	 * @return a future which resolves when all verticles successfully deploy
	 */
	protected Future<CompositeFuture> deployAll(List<VerticleConfiguration> list) {
		return deployAll(list.stream());
	}

	/**
	 * Deploys a stream of {@link VerticleConfiguration VerticleConfigurations}.
	 *
	 * @param stream the stream that contains verticle configurations
	 * @return a future which resolves when all verticles successfully deploy
	 */
	protected Future<CompositeFuture> deployAll(Stream<VerticleConfiguration> stream) {
		return CompositeFuture.join(stream.map(this::deploy).collect(Collectors.toList()));
	}

	/**
	 * Deploys a verticle from a {@link VerticleConfiguration}.
	 * <p>
	 * The configuration contains the classname and configuration that the verticle receive.
	 *
	 * @param configuration the verticle configuration
	 * @return a future which resolves when the verticle successfully deployed
	 */
	protected Future<Deployment> deploy(VerticleConfiguration configuration) {
		logger.info("Deploy verticle: {} [classname: {}, configuration: {}]",
				configuration.name(), configuration.verticle(), configuration.jsonConfig());

		var options = new DeploymentOptions().setConfig(configuration.jsonConfig());
		return launcher.deploy(configuration.verticle(), options);
	}
}
