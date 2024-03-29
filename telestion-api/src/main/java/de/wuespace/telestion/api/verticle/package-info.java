/**
 * <h2>Telestion API - Telestion Verticle</h2>
 *
 * This package contains different classes helping with Vert.x verticles by providing commonly used methods which help
 * to improve the developer experience by massively reducing boiler-plate code.
 * <h3>Usage</h3>
 * Verticles within a Telestion Project should extend {@link de.wuespace.telestion.api.verticle.TelestionVerticle}.
 * It provides different core functionality like more clean start() and stop() methods.
 * <p>
 * A Verticle usually needs an implementation of {@link de.wuespace.telestion.api.verticle.TelestionConfiguration}. They must be created manually - the convention is to create a Record
 * in the same class called Configuration. By inferring the generics of the
 * {@link de.wuespace.telestion.api.verticle.TelestionVerticle} with this {@code Configuration}, the loading code for
 * configurations gets reduced massively.<br/>
 * When using no {@code Configuration} it is recommended to use {@link de.wuespace.telestion.api.verticle.NoConfiguration}
 * which is telling the implementation, that a configuration is not needed.
 * If you need to use dynamic or untyped configuration parameters, use {@link de.wuespace.telestion.api.verticle.UntypedConfiguration}.
 * <p>
 * By implementing {@link de.wuespace.telestion.api.verticle.trait traits} the functionality of the
 * {@link de.wuespace.telestion.api.verticle.TelestionVerticle} can be increased even more.
 * <h3>Examples</h3>
 * For examples refer to the {@code de.wuespace.telestion.example-package}.
 * <p>
 * (c) WueSpace e.V.
 *
 * @see de.wuespace.telestion.api
 * @see de.wuespace.telestion.api.verticle.trait
 */
package de.wuespace.telestion.api.verticle;
