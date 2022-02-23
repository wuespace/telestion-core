package de.wuespace.telestion.application.launcher;

import de.wuespace.telestion.application.deployment.Deployment;
import de.wuespace.telestion.application.loader.Loader;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reference implementation of a {@link ConfigLauncher} and {@link DeploymentLauncher}.
 * <p>
 * It loads the configuration from {@code $PWD/telestion.json} by default, parses it with the
 * {@link TelestionConfigParser} and runs all {@link Loader Loaders} configured in the {@code telestion.json}.
 * <p>
 * Use this launcher in your {@code build.gradle} as main class:
 * <pre>
 * {@code
 * // [...]
 *
 * application {
 *     mainClass.set('de.wuespace.telestion.application.launcher.TelestionLauncher')
 * }
 *
 * // [...]
 * }
 * </pre>
 *
 * @author Ludwig Richter (@fussel178)
 * @see ConfigLauncher
 * @see DeploymentLauncher
 * @see Launcher
 * @see TelestionConfigParser
 * @see Loader
 */
public class TelestionLauncher implements ConfigDeploymentLauncher<JsonObject> {

	/**
	 * Default path to the main configuration file of the {@link TelestionLauncher}.
	 */
	public static final Path DEFAULT_MAIN_CONFIG_PATH = Path.of("conf", "telestion.json");

	/**
	 * This method is the starting point in the {@link TelestionLauncher}.
	 */
	public static void main(String[] args) throws Exception {
		TelestionLauncher launcher = args.length > 0
				// if args are given, interpret as path to main configuration file
				? new TelestionLauncher(Path.of(args[0]))
				: new TelestionLauncher();

		Launcher.main(launcher);
	}

	/**
	 * Path to the main configuration.
	 */
	private final Path mainConfigPath;

	/**
	 * The parsed main configuration in a dynamic {@link JsonObject} format.
	 */
	private JsonObject mainConfiguration;

	/**
	 * The parsed {@link Loader Loaders} from the main configuration.
	 */
	private List<Loader<?>> loaders;

	/**
	 * The {@link Deployment Deployments} which run on the controlled {@link Vertx} instance.
	 */
	private final List<Deployment> deployments = new ArrayList<>();

	/**
	 * The {@link VertxOptions} that configure the controlled {@link Vertx} instance.
	 */
	private VertxOptions vertxOptions;

	/**
	 * The {@link Vertx} instance controlled by the {@link TelestionLauncher}.
	 */
	private Vertx vertx;

	/**
	 * Creates a new {@link TelestionLauncher} and loads and parses the main configuration from the specified path.
	 *
	 * @param mainConfigPath path to the main configuration
	 */
	public TelestionLauncher(Path mainConfigPath) {
		this.mainConfigPath = mainConfigPath;
	}

	/**
	 * Creates a new {@link TelestionLauncher} and loads and parses the main configuration from the default path.
	 *
	 * @see #DEFAULT_MAIN_CONFIG_PATH
	 */
	public TelestionLauncher() {
		this(DEFAULT_MAIN_CONFIG_PATH);
	}

	@Override
	public Path getMainConfigPath() {
		return mainConfigPath;
	}

	@Override
	public JsonObject getMainConfiguration() {
		return mainConfiguration;
	}

	@Override
	public List<Loader<?>> getLoaders() {
		return loaders;
	}

	@Override
	public List<Deployment> getDeployments() {
		return deployments;
	}

	@Override
	public Deployment addDeployment(Deployment deployment) {
		deployments.add(deployment);
		return deployment;
	}

	@Override
	public VertxOptions getVertxOptions() {
		return vertxOptions;
	}

	@Override
	public Vertx getVertx() {
		return vertx;
	}

	@Override
	public Future<Void> start() throws Exception {
		printBanner();
		printVersion();
		logger.debug("Start TelestionLauncher");

		// parse main config
		mainConfiguration = readMainConfig();
		var parser = new TelestionConfigParser(mainConfiguration);
		loaders = parser.parseLoaders();
		vertxOptions = parser.parseVertxOptions();
		boolean isClustered = parser.parseIsClustered();

		// 1 - initialize loaders and call init event
		return Future.future(promise -> initializeLoaders()
				// 2 - call before vertx startup event
				.compose(result -> Loader.call(loaders, Loader::onBeforeVertxStartup))
				// 3 - create vertx instance
				.compose(result -> createVertxInstance(isClustered))
				// 4 - call after vertx startup event
				.compose(result -> Loader.call(loaders, Loader::onAfterVertxStartup))
				// fix future type mismatch
				.onSuccess(result -> promise.complete())
				.onFailure(promise::fail));
	}

	@Override
	public Future<Void> stop() {
		logger.debug("Stop TelestionLauncher");
		// 1 - call before Vertx shutdown event
		return Future.future(promise -> Loader.call(loaders, Loader::onBeforeVertxShutdown)
				// 2 - close Vertx instance
				.compose(result -> vertx.close())
				// 3 - call after Vertx shutdown event
				.compose(result -> Loader.call(loaders, Loader::onAfterVertxShutdown))
				// 4 - clear Vertx instance from launcher
				.compose(result -> clearVertxInstance())
				// 5 - call exit event
				.compose(result -> Loader.call(loaders, Loader::onExit))
				// fix future type mismatch
				.onSuccess(result -> promise.complete())
				.onFailure(promise::fail));
	}

	/**
	 * Reads the main configuration from the configured {@link #mainConfigPath}, parses it as a dynamic
	 * {@link JsonObject} and returns it.
	 *
	 * @return the parsed main configuration as a dynamic {@link JsonObject}
	 */
	private JsonObject readMainConfig() throws IOException {
		logger.debug("Read main configuration from {}", mainConfigPath);
		return new JsonObject(Files.readString(mainConfigPath));
	}

	/**
	 * Initializes a registered {@link Loader Loaders} in this {@link TelestionLauncher} instance.
	 * <p>
	 * It returns a {@link Future} which resolves when all loaders are initialized.
	 *
	 * @return a future which resolves on successful loader initialization
	 */
	private Future<CompositeFuture> initializeLoaders() {
		logger.debug("Initialize loaders");
		loaders.forEach(loader -> loader.init(this));
		return Loader.call(loaders, Loader::onInit);
	}

	/**
	 * Creates a new {@link Vertx} instance with the {@link #vertxOptions} from the {@link TelestionLauncher} instance.
	 * <p>
	 * A future returns the instance when it resolves.
	 *
	 * @param isClustered when {@code true} the Vertx instance should be clustered
	 * @return a future which resolves with the Vertx instance
	 */
	private Future<Vertx> createVertxInstance(boolean isClustered) {
		logger.debug("Create Vertx instance");
		logger.debug("Is clustered: {}", isClustered);
		logger.debug("Vertx options: {}", vertxOptions);
		var future = isClustered
				? Vertx.clusteredVertx(vertxOptions)
				: Future.succeededFuture(Vertx.vertx(vertxOptions));

		return future.onSuccess(vertx -> {
			vertx.exceptionHandler(cause -> Loader.call(loaders,
					(loader, promise) -> loader.onVertxException(promise, cause)));
			this.vertx = vertx;
			logger.info("Created Vertx instance");
		}).onFailure(cause -> logger.error("Created Vertx instance with errors:", cause));
	}

	/**
	 * Clears the {@link #vertx} instance from the {@link TelestionLauncher} instance.
	 *
	 * @return a future which resolves when the {@link #vertx} instance clears
	 */
	private Future<Void> clearVertxInstance() {
		logger.debug("Clear Vertx instance");
		this.vertx = null;
		return Future.succeededFuture();
	}

	/**
	 * Prints the Telestion banner on the console.
	 */
	private static void printBanner() {
		System.out.println("""

				             .%%%%%#*-
				       .      .....-+%@*.                             .
				      *@-    .%%%%#+. :%@+       -*********           @=                     .   .#-
				     %@- :+.  .  .-*@%. +@*       ...*@:...           @=                    -@.   .
				    *@= -%%#+.      .%@: *@=         +@      :*###=   @=   +###*.  .+###*. *#@#+  #-   +#*#*:  .#=+*##:
				    @@  ####%##@@+   :@% :@#         +@     :@=  .@+ .@=  %#   *%  %@   **  -@:  .@=  %#.  =@- .@#: .#@
				    @@  ######@@@*   :@# :@#         +@     #@=++=%@  @= -@*+++*@- =%*+-.   -@.  .@= =@.    #% .@-   =@
				    *@= -%########+.                 +@     #@:---:.  @= -@=:--:.    :=+%*  -@.  .@= =@.    %% .@-   +@
				     %@- :*%%%####%%+                +@     -@=   =: .@+  %#.  :=  %*   #@  -@-  .@=  %#. .=@- .@-   +@
				      *@#. .=+****+-. -:             =#      :*###*.  #-   +###*-  :*###*.   *#*  #-   +#*#*:  .#:   =#
				       :#@%+-.....:-+%@*
				         .-*%%%%%%%#*-
				""");
	}

	/**
	 * Logs the package version to the console.
	 */
	private static void printVersion() {
		var version = TelestionLauncher.class.getPackage().getSpecificationVersion();
		version = Objects.requireNonNullElse(version, "X.X.X");
		logger.info("Telestion v{}", version);
	}

	private static final Logger logger = LoggerFactory.getLogger(TelestionLauncher.class);
}
