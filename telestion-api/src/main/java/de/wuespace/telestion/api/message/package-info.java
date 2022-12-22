/**
 * <h2>Telestion API - Message</h2>
 *
 * Contains helper classes for the message handling for the Vert.x event bus system.<br/>
 * This package adds support for Java-15 Records.
 * <h3>Structure</h3>
 * In the base package exist classes handling the general structure for messages which are sent via the event bus of
 * Vert.x. JSON (Jackson Codec) is used to encode and decode messages for the event bus.
 * <p>
 * {@link de.wuespace.telestion.api.message.HeaderInformation} add support for message headers. They can be
 * used to include metadata unrelated to the payload of a message.
 * <p>
 * It is heavily recommended to use Telestion traits like {@link de.wuespace.telestion.api.verticle.trait.WithEventBus}, it contains different
 * helper methods for message serialization.
 * <p>
 * (c) WueSpace e.V.
 */
package de.wuespace.telestion.api.message;
