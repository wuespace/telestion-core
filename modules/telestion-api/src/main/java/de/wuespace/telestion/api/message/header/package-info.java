/**
 * <h2>Telestion API - Header</h2>
 *
 * Contains classes helping to create headers for Vert.X messages.
 * <p>
 * Headers can be used in different scenarios. They are usually used to provide more details to a message which are not
 * part of the message itself. This can be a hint to where it belongs to or timestamps or even where it is supposed to
 * go in the end.<br/>
 * An example can be found in the connection api in the {@code de.wuespace.telestion.services.connection} module.<br/>
 * The classes of this package are used to add support for Java-15 Records for headers.
 * <h3>Usage</h3>
 * Headers are extending
 * {@link de.wuespace.telestion.api.message.header.Information} which in combination with
 * {@link de.wuespace.telestion.api.message.header.serialization.SerializationInfo} annotated record components are
 * used to portray the header data.
 * <p>
 * The {@link de.wuespace.telestion.api.message.header.Information information-objects} can be serialized with the
 * methods of {@link de.wuespace.telestion.api.message.header.InformationUtils}. They will yield a
 * {@link io.vertx.core.MultiMap} which represent the header. They are used in combination with the
 * {@link io.vertx.core.eventbus.DeliveryOptions} from Vert.X.
 * <p>
 * The usage of {@link de.wuespace.telestion.api.verticle.trait.WithEventBus} is recommended as it provides helper
 * methods for headers, but it is not required.
 * <p>
 * Because {@link io.vertx.core.MultiMap headers} require keys which should be consistent for a good user experience,
 * proposals for names can be found in {@link de.wuespace.telestion.api.message.header.serialization.CommonNames}.<br/>
 * Those proposals are not mandatory but highly recommended.
 * <h3>Examples</h3>
 * To see how to use this api, refer to the examples from {@code de.wuespace.telestion.example}, namely
 * {@code InformationSender} and {@code InformationReceiver} which show how to send and receive headers.
 * <p>
 * (c) WueSpace e.V.
 *
 * @see de.wuespace.telestion.api.message
 */
package de.wuespace.telestion.api.message.header;
