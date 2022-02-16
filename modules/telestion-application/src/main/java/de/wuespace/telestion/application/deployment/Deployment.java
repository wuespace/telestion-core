package de.wuespace.telestion.application.deployment;

import de.wuespace.telestion.application.launcher.LoaderLauncher;
import de.wuespace.telestion.application.loader.Loader;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;

import java.util.Objects;

/**
 * A {@link Deployment} represents a {@link Verticle} in a {@link io.vertx.core.Vertx Vertx} instance.
 * <p>
 * The {@link Verticle} can be deployed and un-deployed on the {@link io.vertx.core.Vertx Vertx} instance.
 * <p>
 * A {@link Verticle} can be represented by:
 * <ul>
 *     <li>a {@link Verticle} instance</li>
 *     <li>a {@link Class} type</li>
 *     <li>a classname which points the the verticle class</li>
 * </ul>
 * (see {@link DeploymentType})
 *
 * @author Ludwig Richter (@fussel178)
 * @see io.vertx.core.Vertx
 */
public class Deployment {

	/**
	 * A reference to the {@link LoaderLauncher} which holds the {@link Deployment Deployments}.
	 */
	private final LoaderLauncher launcher;

	/**
	 * Is one of the 3 deployment types (see {@link DeploymentType}).
	 * <p>
	 * Contains the verticle {@link Class} type.
	 */
	private final Class<? extends Verticle> verticleClass;

	/**
	 * Is one of the 3 deployment types (see {@link DeploymentType}).
	 * <p>
	 * Contains an instance of a verticle.
	 */
	private final Verticle verticle;

	/**
	 * Is one of the 3 deployment types (see {@link DeploymentType}).
	 * <p>
	 * Contains the classname to a verticle class.
	 */
	private final String className;

	/**
	 * Deployment options that the verticle receives during deployment on the
	 * {@link io.vertx.core.Vertx Vertx} instance.
	 */
	private final DeploymentOptions options;

	/**
	 * Contains the deployment id of the verticle on the {@link io.vertx.core.Vertx Vertx} instance.
	 * Is {@code null} when the verticle in this deployment is currently not deployed.
	 */
	private String deploymentId = null;

	private Deployment(
			LoaderLauncher launcher,
			Class<? extends Verticle> verticleClass,
			Verticle verticle,
			String className,
			DeploymentOptions options) {

		this.launcher = launcher;
		this.verticleClass = verticleClass;
		this.verticle = verticle;
		this.className = className;
		this.options = options;
	}

	/**
	 * Like {@link #Deployment(LoaderLauncher, Class)} but with {@link DeploymentOptions}.
	 * <p>
	 * These deployment options receive the {@link Verticle} instance on deployment.
	 *
	 * @param launcher      the launcher that controls the {@link io.vertx.core.Vertx Vertx} instance
	 * @param verticleClass the class type of the {@link Verticle verticle} that you want deploy
	 * @param options       deployment options for your running verticle
	 */
	public Deployment(LoaderLauncher launcher, Class<? extends Verticle> verticleClass, DeploymentOptions options) {
		this(launcher, verticleClass, null, null, options);
	}

	/**
	 * Like {@link #Deployment(LoaderLauncher, Verticle)} but the {@link Verticle} instance is created
	 * by invoking the default constructor.
	 *
	 * @param launcher      the launcher that controls the {@link io.vertx.core.Vertx Vertx} instance
	 * @param verticleClass the class type of the {@link Verticle verticle} that you want deploy
	 */
	public Deployment(LoaderLauncher launcher, Class<? extends Verticle> verticleClass) {
		this(launcher, verticleClass, new DeploymentOptions());
	}

	/**
	 * Like {@link #Deployment(LoaderLauncher, Verticle)} but with {@link DeploymentOptions}.
	 * <p>
	 * These deployment options receive the {@link Verticle} instance on deployment.
	 *
	 * @param launcher the launcher that controls the {@link io.vertx.core.Vertx Vertx} instance
	 * @param verticle your verticle instance that you want to deploy
	 * @param options  deployment options for your running verticle
	 */
	public Deployment(LoaderLauncher launcher, Verticle verticle, DeploymentOptions options) {
		this(launcher, null, verticle, null, options);
	}

	/**
	 * Creates a new deployment instance with a verticle instance that you create by yourself.
	 *
	 * @param launcher the launcher that controls the {@link io.vertx.core.Vertx Vertx} instance
	 * @param verticle your verticle instance that you want to deploy
	 */
	public Deployment(LoaderLauncher launcher, Verticle verticle) {
		this(launcher, verticle, new DeploymentOptions());
	}

	/**
	 * Like {@link #Deployment(LoaderLauncher, String)} but with {@link DeploymentOptions}.
	 * <p>
	 * These deployment options receive the {@link Verticle} instance on deployment.
	 *
	 * @param launcher  the launcher that controls the {@link io.vertx.core.Vertx Vertx} instance
	 * @param className the classname or binary name of your {@link Verticle verticle} class
	 * @param options   deployment options for your running verticle
	 */
	public Deployment(LoaderLauncher launcher, String className, DeploymentOptions options) {
		this(launcher, null, null, className, options);
	}

	/**
	 * Like {@link #Deployment(LoaderLauncher, Verticle)} but the {@link Verticle verticle} instance is created
	 * by a {@link io.vertx.core.spi.VerticleFactory verticle factory} based on the classname.
	 *
	 * @param launcher  the launcher that controls the {@link io.vertx.core.Vertx Vertx} instance
	 * @param className the classname or binary name of your {@link Verticle verticle} class
	 */
	public Deployment(LoaderLauncher launcher, String className) {
		this(launcher, className, new DeploymentOptions());
	}

	/**
	 * Get the {@link DeploymentType} based on the given parameters in the constructor.
	 *
	 * @return the deployment type of this instance
	 */
	public DeploymentType getDeploymentType() {
		if (Objects.nonNull(verticleClass)) {
			return DeploymentType.CLASS_TYPE;
		} else if (Objects.nonNull(verticle)) {
			return DeploymentType.INSTANCE;
		} else if (Objects.nonNull(className)) {
			return DeploymentType.CLASS_NAME;
		} else {
			throw new IllegalStateException("Unexpected state entered while trying to determine deployment type. " +
					"Sorry for the inconvenience. There should only constructors exist that should set at least one " +
					"specific deployment member. Please help us fix the mistake by filing an issue at " +
					"https://github.com/wuespace/telestion-core/issues/new and include the entire error message " +
					"(including the details below) and we'll have a look as soon as we can. Details: " + this);
		}
	}

	/**
	 * Is {@code true} when the verticle runs on the {@link io.vertx.core.Vertx Vertx} instance.
	 *
	 * @return {@code true} when the verticle is deployed
	 */
	public boolean isDeployed() {
		return Objects.nonNull(deploymentId);
	}

	/**
	 * Get a reference to the {@link LoaderLauncher} instance that holds this deployment and controls the
	 * {@link io.vertx.core.Vertx Vertx} instance.
	 *
	 * @return a reference to the associated launcher instance
	 */
	public LoaderLauncher getLauncher() {
		return launcher;
	}

	/**
	 * Get the {@link Class} type of the verticle.
	 * <p>
	 * Can be {@code null} depending on the given parameters in the constructor.
	 * Check first with {@link #getDeploymentType()} if the deployment type is a {@link DeploymentType#CLASS_TYPE}
	 * before continuing.
	 *
	 * @return the {@link Class} type of the verticle
	 */
	public Class<? extends Verticle> getVerticleClass() {
		return verticleClass;
	}

	/**
	 * Get the verticle instance.
	 * <p>
	 * Can be {@code null} depending on the given parameters in the constructor.
	 * Check first with {@link #getDeploymentType()} if the deployment type is a {@link DeploymentType#INSTANCE}
	 * before continuing.
	 *
	 * @return the verticle instance
	 */
	public Verticle getVerticle() {
		return verticle;
	}

	/**
	 * Get the classname or binary name of the verticle class.
	 * <p>
	 * Can be {@code null} depending on the given parameters in the constructor.
	 * Check first with {@link #getDeploymentType()} if the deployment type is a {@link DeploymentType#CLASS_NAME}
	 * before continuing.
	 *
	 * @return the classname or binary name of the verticle class
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Get the deployment options that receives the {@link Verticle} instance on deployment
	 *
	 * @return the deployment options for the verticle instance
	 */
	public DeploymentOptions getDeploymentOptions() {
		return options;
	}

	/**
	 * Get the deployment id of the verticle in the {@link io.vertx.core.Vertx Vertx} instance.
	 * <p>
	 * Can be {@code null} when the verticle is not deployed yet.
	 *
	 * @return the deployment id of the verticle in the Vertx instance
	 */
	public String getDeploymentId() {
		return deploymentId;
	}

	/**
	 * Deploys this deployment on the {@link io.vertx.core.Vertx Vertx} instance
	 * controlled by the {@link LoaderLauncher}.
	 *
	 * @return a future which resolves when this instance successfully deploys
	 */
	public Future<Deployment> deploy() {
		var loaders = launcher.getLoaders();

		// 1 - call before verticle deploy event
		return Future.future(promise -> Loader.call(loaders, (loader, loaderPromise) -> loader.onBeforeVerticleDeploy(loaderPromise, this))
				// 2 - deploy verticle on Vert.x instance
				.compose(result -> deployVerticle())
				// 3 - call after verticle deploy event
				.compose(result -> Loader.call(loaders, (loader, loaderPromise) -> loader.onAfterVerticleDeploy(loaderPromise, this)))
				// return instance to satisfy promise
				.map(result -> this).onComplete(promise));
	}

	/**
	 * Un-deploys this deployment from the {@link io.vertx.core.Vertx Vertx} instance
	 * controlled by the {@link LoaderLauncher}.
	 *
	 * @return a future which resolves when this instance successfully un-deploys
	 */
	public Future<Deployment> undeploy() {
		var loaders = launcher.getLoaders();

		// 1 - call before verticle undeploy event
		return Future.future(promise -> Loader.call(loaders, (loader, loaderPromise) -> loader.onBeforeVerticleUndeploy(loaderPromise, this))
				// 2 - undeploy verticle on Vert.x instance
				.compose(result -> undeployVerticle())
				// 3 - call after verticle undeploy event
				.compose(result -> Loader.call(loaders, (loader, loaderPromise) -> loader.onAfterVerticleUndeploy(loaderPromise, this)))
				// return instance to satisfy promise
				.map(result -> this).onComplete(promise));
	}

	/**
	 * Deploys the verticle on the associated {@link #launcher}.
	 *
	 * @return a future which resolves with the deployment id when the verticle successfully deploys
	 */
	private Future<String> deployVerticle() {
		var vertx = launcher.getVertx();

		var future = switch (getDeploymentType()) {
			case CLASS_TYPE -> vertx.deployVerticle(verticleClass, options);
			case INSTANCE -> vertx.deployVerticle(verticle, options);
			case CLASS_NAME -> vertx.deployVerticle(className, options);
		};

		return future.onSuccess(this::setDeploymentId);
	}

	/**
	 * Un-deploys the verticle from the associated {@link #launcher}.
	 *
	 * @return a future which resolves when the verticle successfully un-deploys
	 */
	private Future<Void> undeployVerticle() {
		// fail fast if verticle is not deployed
		if (!isDeployed()) return Future.failedFuture("Not deployed");

		return launcher.getVertx().undeploy(deploymentId).onSuccess(this::clearDeploymentId);
	}

	/**
	 * Set the deployment id of the verticle in the {@link io.vertx.core.Vertx Vertx} instance.
	 *
	 * @param deploymentId the new deployment id
	 */
	private void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	/**
	 * Remove the deployment id in this instance.
	 */
	private void clearDeploymentId(Void result) {
		setDeploymentId(null);
	}
}
