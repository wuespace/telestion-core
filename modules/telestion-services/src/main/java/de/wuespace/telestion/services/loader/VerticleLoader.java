package de.wuespace.telestion.services.loader;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.services.loader.verticle.AbstractVerticleLoader;
import de.wuespace.telestion.services.loader.verticle.VerticleLoaderConfiguration;
import io.vertx.core.Promise;

/**
 * <h2>Description</h2>
 * The {@link VerticleLoader} reads {@link de.wuespace.telestion.services.loader.verticle.VerticleConfiguration VerticleConfigurations}
 * from the main configuration and deploys them on the associated launcher.
 *
 * <h2>Usage</h2>
 * Append the classname of the {@link VerticleLoader} to the main configuration and add verticle configurations
 * to the `"verticles": []`:
 * <pre>
 * {@code
 * {
 *   "loaders: [
 *     "de.wuespace.telestion.services.loader.VerticleLoader"
 *   ],
 *   "verticles": [
 *     {
 *       "name": "My Verticle",
 *       "verticle": "de.wuespace.telestion.project.playground.MyVerticle"
 *       "magnitude": 1,
 *       "config": {}
 *     }
 *   ]
 * }
 * }
 * </pre>
 *
 * @author Ludwig Richter (@fussel178)
 * @see de.wuespace.telestion.services.loader.verticle.VerticleConfiguration
 */
public class VerticleLoader extends AbstractVerticleLoader<VerticleLoader.Configuration> {

	/**
	 * Configuration for the {@link VerticleLoader}.
	 *
	 * @param verticlesProperty the property name which points to the verticle configuration
	 */
	public record Configuration(
			@JsonProperty String verticlesProperty
	) implements VerticleLoaderConfiguration {
	}

	@Override
	public void onAfterVertxStartup(Promise<Void> startPromise) throws Exception {
		onConfiguration(launcher.getMainConfiguration()).onComplete(startPromise);
	}
}
