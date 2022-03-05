package de.wuespace.telestion.application.launcher;

import de.wuespace.telestion.application.loader.Loader;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parses a Telestion configuration in the {@link JsonObject} format.
 * It extracts different configuration options like {@link Loader loaders} or {@link VertxOptions Vert.x options}.
 * <p>
 * It's mainly used by the {@link TelestionLauncher} class.
 *
 * @author Ludwig Richter
 */
@SuppressWarnings("ClassCanBeRecord")
public class TelestionConfigParser {

	/**
	 * The name of the JSON property which points to the {@link Vertx#isClustered() is clustered option}
	 * in the Telestion configuration.
	 */
	public static final String IS_CLUSTERED_KEY = "isClustered";

	/**
	 * The name of the JSON property which points to the {@link VertxOptions Vert.x options}
	 * in the Telestion configuration.
	 */
	public static final String VERTX_OPTIONS_KEY = "vertxOptions";

	/**
	 * The name of the JSON property which points to the {@link Loader loaders} that should be loaded
	 * by the {@link TelestionLauncher} in the Telestion configuration.
	 */
	public static final String LOADERS_KEY = "loaders";

	/**
	 * The name of the JSON property which points to the classname of the {@link Loader loader}
	 * in the loaders' entry.
	 */
	public static final String EXTENDED_LOADER_CLASSNAME_KEY = "className";

	/**
	 * The name of the JSON property which points to the {@link Loader loader} configuration
	 * in the loaders' entry.
	 */
	public static final String EXTENDED_LOADER_CONFIG_KEY = "config";

	/**
	 * The received {@link JsonObject} containing the raw configuration received upon creation.
	 */
	private final JsonObject configuration;

	public TelestionConfigParser(JsonObject configuration) {
		this.configuration = configuration;
	}

	/**
	 * Get the {@link JsonObject} containing the raw configuration received upon creation.
	 *
	 * @return the {@link JsonObject} containing the raw configuration
	 */
	public JsonObject getConfiguration() {
		return configuration;
	}

	/**
	 * <h4>Description</h4>
	 * Parses and instantiates a list of {@link Loader Loaders} from the raw configuration and returns them.
	 * The key which is used during extraction is {@link #LOADERS_KEY}.
	 * <p>
	 * There are two supported configuration types:
	 * <ul>
	 *     <li>The <em>basic configuration</em> is a string which represents the classname of the {@link Loader}.</li>
	 *     <li>The <em>extended configuration</em> is a {@link JsonObject} which contains the classname and additional
	 *     configuration options that are passed to the {@link Loader} upon creation.</li>
	 * </ul>
	 *
	 * <h4>Configuration example</h4>
	 * <p>
	 * {@code telestion.json}:
	 * <pre>
	 * {@code
	 * {
	 *   "loaders": [
	 *     "de.wuespace.telestion.services.loader.DebugLoader",
	 *     {
	 *       "className": "de.wuespace.telestion.services.loader.ConfigLoader",
	 *       "config": {
	 *         "includeDefaultStores": true
	 *       }
	 *     }
	 *   ]
	 * }
	 * }
	 * </pre>
	 *
	 * @return a list of instantiated loaders based on the Telestion configuration
	 */
	public List<Loader<?>> parseLoaders()
			throws IllegalArgumentException, ClassNotFoundException, InvocationTargetException, InstantiationException,
			IllegalAccessException, NoSuchMethodException {
		JsonArray loadersConfig;
		try {
			loadersConfig = configuration.getJsonArray(LOADERS_KEY);
		} catch (ClassCastException e) {
			throw new ClassCastException(("Cannot read loader configuration from property %s. Is the loader " +
					"configuration a JSON array?").formatted(LOADERS_KEY));
		}
		logger.debug("Extracted loaders configuration: {}", loadersConfig);

		List<Loader<?>> loaders = new ArrayList<>();
		for (Object entry : loadersConfig) {
			loaders.add(parseLoaderConfig(entry));
		}
		logger.debug("Created loaders: {}", loaders);

		return loaders;
	}

	/**
	 * Parses the {@link VertxOptions} from the raw configuration and returns them.
	 * The key which is used during extraction is {@link #VERTX_OPTIONS_KEY}.
	 *
	 * @return the parsed {@link VertxOptions Vert.x options}
	 */
	public VertxOptions parseVertxOptions() {
		var vertxOptionsConfig = configuration.getJsonObject(VERTX_OPTIONS_KEY, new JsonObject());
		logger.debug("Vertx options configuration: {}", vertxOptionsConfig);
		return new VertxOptions(vertxOptionsConfig);
	}

	/**
	 * Parses the {@link Vertx#isClustered()} from the raw configuration and returns it.
	 * The key which is used during extraction is {@link #IS_CLUSTERED_KEY}.
	 *
	 * @return the parsed {@link Vertx#isClustered()} option
	 */
	public boolean parseIsClustered() {
		var isClustered = configuration.getBoolean(IS_CLUSTERED_KEY, false);
		logger.debug("Is clustered configuration: {}", isClustered);
		return isClustered;
	}

	/**
	 * Receives a {@link Loader} entry from the {@link Loader Loaders} configuration, tries to determine
	 * the configuration type (either basic or extended) and creates an instance of the specified loader.
	 * <p>
	 * If the extended configuration variant is used, the loader additionally receives the parsed configuration.
	 * The keys that are used during extraction are {@link #EXTENDED_LOADER_CLASSNAME_KEY}
	 * and {@link #EXTENDED_LOADER_CONFIG_KEY}.
	 *
	 * @param entry the {@link Loader} entry from the {@link Loader Loaders} configuration
	 * @return the instantiated loader
	 */
	private Loader<?> parseLoaderConfig(Object entry)
			throws IllegalArgumentException, ClassNotFoundException, InvocationTargetException, InstantiationException,
			IllegalAccessException, NoSuchMethodException {

		if (entry instanceof JsonObject) {
			logger.debug("Extended loader configuration: {}", entry);

			var className = ((JsonObject) entry).getString(EXTENDED_LOADER_CLASSNAME_KEY);
			if (Objects.isNull(className)) {
				throw new IllegalArgumentException(("The extended loader configuration %s does not contain a " +
						"classname on the \"%s\" property. Please provide one to continue.")
						.formatted(entry, EXTENDED_LOADER_CLASSNAME_KEY));
			}
			logger.debug("Parsed classname: {}", className);

			var config = ((JsonObject) entry).getJsonObject(EXTENDED_LOADER_CONFIG_KEY);
			if (Objects.isNull(config)) {
				throw new IllegalArgumentException(("The extended loader configuration %s does not contain a " +
						"configuration for the loader on the \"%s\" property. Please provide one to continue.")
						.formatted(entry, EXTENDED_LOADER_CLASSNAME_KEY));
			}
			logger.debug("Parsed configuration: {}", config);

			var loader = Loader.instantiate(className);
			loader.setConfig(config);
			return loader;
		} else if (entry instanceof String) {
			logger.debug("Basic loader configuration: {}", entry);

			return Loader.instantiate((String) entry);
		} else {
			throw new IllegalArgumentException(("Found unknown loader configuration type %s. Please change it to a " +
					"valid loader configuration format or remove it to continue.").formatted(entry));
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(TelestionConfigParser.class);
}
