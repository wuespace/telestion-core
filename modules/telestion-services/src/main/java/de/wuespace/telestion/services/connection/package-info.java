/**
 * <h2>Connection API</h2>
 *
 * <h3>Short description:</h3>
 * <p>
 *     This module contains the connection api.<br>
 *     It provides a simplified interface for the most commonly used network interfaces in ground-stations and is
 *     optimized for the VertX framework.
 * </p>
 *
 * <h3>Supported network interfaces:</h3>
 * <ul>
 *     <li>TCP</li>
 *     <li>UDP</li>
 *     <li>Serial connection (e.g. UART)</li>
 * </ul>
 *
 * <h3>General Concepts:</h3>
 * <p>
 *     The general concept of the connection api is to allow verticles to implement networking code without knowing the
 *     specifics of the interface itself. This is achieved by the following concepts:
 * </p>
 * <ul>
 *     <li>Sender-Api</li>
 *     <li>Receiver-Api</li>
 *     <li>Static-Sending</li>
 *     <li>Special Concepts</li>
 *     <li>Manual Mode</li>
 * </ul>
 *
 * <h4>{@link de.wuespace.telestion.services.connection.Sender Sender-Api}:</h4>
 * <p>
 *		{@link de.wuespace.telestion.services.connection.ConnectionData} or
 *		{@link de.wuespace.telestion.services.connection.SenderData} sent to this class will be rerouted to the
 *		designated network interface or their dispatchers. If a connection is not, yet, established the default
 *		behaviour is to try to create a new connection to the target.
 * </p>
 * <p>
 *     <em>
 *         Note:<br>
 *         This can fail and the package can be dropped if the targeted network interface controller does not support
 *         informing about failed packet delivery!
 * 	   </em>
 * </p>
 *
 * <h4>{@link de.wuespace.telestion.services.connection.Receiver Receiver-Api}:</h4>
 * <p>
 *     If this interface is used all incoming {@link de.wuespace.telestion.services.connection.ConnectionData packages}
 *     will be routed to its output address.<br>
 *     This allows parsers to listen on multiple network connections at once. Answers then can be sent to the
 *     Sender-Api again.
 * </p>
 *
 * <h4>{@link de.wuespace.telestion.services.connection.StaticSender Static-Sending}:</h4>
 * <p>
 *     In cases where the receiver is already known at start-up, the
 *     {@link de.wuespace.telestion.services.connection.StaticSender} which can be fed with
 *     {@link de.wuespace.telestion.services.connection.RawMessage RawMessages} which do not require network interface
 *     information.<br>
 *     The static sender will automatically send the packages to the connection specified in its config.
 * </p>
 *
 * <h4>Special Concepts:</h4>
 * <p>
 *     There are more complex concepts than the previously described ones. This allows for more advanced or network
 *     specific structures.
 * </p>
 * <h5>Connection established:</h5>
 * <p>
 *     There are cases when one needs to know when a new connection has been established. In this case the XXXX will
 *     yield the {@link de.wuespace.telestion.services.connection.ConnectionDetails} specifying the connection.
 * </p>
 *
 * <h5>{@link de.wuespace.telestion.services.connection.Broadcaster Broadcasting}:</h5>
 * <p>
 *     To send information to all active network interfaces (which support broadcasting), a
 *     {@link de.wuespace.telestion.services.connection.Broadcaster} verticle needs to be added.
 * </p>
 *
 * <h4>Manual Mode:</h4>
 * <p>
 *     In rare cases where the previously described abstractions cannot be applied,
 *     there is still the possibility to use the network interfaces directly. For this refer to the package
 *     descriptions of the designated packages.
 * </p>
 *
 * @since 0.2.0
 * @version 1.0
 * @author Cedric Boes (cb0s)
 */
package de.wuespace.telestion.services.connection;
