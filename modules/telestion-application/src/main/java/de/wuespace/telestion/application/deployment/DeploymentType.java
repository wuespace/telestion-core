package de.wuespace.telestion.application.deployment;

/**
 * The {@link DeploymentType} specifies which information the {@link Deployment} has
 * to deploy a verticle on a {@link io.vertx.core.Vertx Vertx} instance.
 *
 * The following types are supported:
 *
 * <table>
 *     <tr>
 *         <th>Deployment type</th>
 *         <th>associated information</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #INSTANCE}</td>
 *         <td>a {@link io.vertx.core.Verticle Verticle} instance</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #CLASS_TYPE}</td>
 *         <td>a verticle {@link Class} type</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #CLASS_NAME}</td>
 *         <td>a classname of binary name of a verticle class</td>
 *     </tr>
 * </table>
 *
 * @author Ludwig Richter (@fussel178)
 */
public enum DeploymentType {
	/**
	 * Represents a verticle {@link Class} type as information in the {@link Deployment}.
	 */
	CLASS_TYPE,
	/**
	 * Represents a {@link io.vertx.core.Verticle Verticle} instance as information
	 * in the {@link Deployment}.
	 */
	INSTANCE,
	/**
	 * Represents a classname of binary name of a verticle class as information
	 * in the {@link Deployment}.
	 */
	CLASS_NAME
}
