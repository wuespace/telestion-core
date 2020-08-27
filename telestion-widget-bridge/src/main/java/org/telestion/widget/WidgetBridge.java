package org.telestion.widget;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.message.Address;
import org.telestion.core.verticle.RandomPositionPublisher;

/**
 * WidgetBridge is a verticle which uses SockJS-WebSockets to extend the
 * vertx.eventBus() to an HTTP-Server.
 * <p>
 *     It creates <code>Router</code> using vertx, which handles HTTP-Requests
 *     coming to the HTTP-Server.
 *     The EventBusBridge by default works after the deny-any-principle, which
 *     means that any message which is not permitted explicitly will be denied (reply messages are an exception).
 *     To permit the desired messages for the frontend to go through you have to configure
 *     <code>SockJSBridgeOptions</code> and mount them as a SubRouter to the Router.
 *
 *     You have to configure the following options:
 *     <ul>
 *         <li>httpServerOptions - the HttpServerOptions to configure the HTTP-Server</li>
 *         <li>defaultSockJSBridgeOptions - the SockJSBridgeOptions to configure rules to allow messages to go through</li>
 *     </ul>
 *     To do that you have four constructors to initialize the HTTP-Server.
 * </p>
 * <p>
 *     To define rules for messages to be allowed through
 *     you have to add <code>new PermittedOptions()</code> to the sockJSBridgeOptions.
 *     You can either do that in the third constructor
 *     by passing <code>new SockJSBridgeOptions</code> as parameter
 *     or modify the defaultSockJSBridgeOptions and add your rules there.
 * </p>
 * <p>
 * An example looks like this:
 * <pre>
 * {@code
 *       SockJSBridgeOptions defaultSockJSBridgeOptions = new SockJSBridgeOptions()
 *          .addInboundPermitted(new PermittedOptions()
 *              .setAddress(Address.incoming(<YourClass>.class, "<method>")))
 *          .addOutboundPermitted(new PermittedOptions()
 *              .setAddress(Address.outgoing(<YourClass>.class, "<method>)));
 * }
 * </pre>
 * </p>
 *
 * {@link ./README.md} for more information
 */
public final class WidgetBridge extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(WidgetBridge.class);
    private final HttpServerOptions httpOptions;
    // TODO: think about the implementation of sockJSBridgeOptions with default
    private final SockJSBridgeOptions sockJSBridgeOptions;
    private final SockJSBridgeOptions defaultSockJSBridgeOptions = new SockJSBridgeOptions()
            .addInboundPermitted(new PermittedOptions()
                .setAddress(Address.outgoing(RandomPositionPublisher.class, "MockPos")));

    /**
     * This constructor supplies default options
     * and uses the defaultSockJSBridgeOptions for the applied rules.
     *
     * @param host the ip address of the host on which the HTTP-Server should run
     * @param port the port on which the HTTP-Server should listen
     */
    public WidgetBridge(String host, int port) {
        this.httpOptions = new HttpServerOptions()
                .setHost(host)
                .setPort(port);
        this.sockJSBridgeOptions = this.defaultSockJSBridgeOptions;
    }

    /**
     * This constructor supplies default options
     * and uses the defaultSockJSBridgeOptions for the applied rules.
     *
     * @param httpOptions the HttpServerOptions to create the HTTP-Server (e.g., <code>new HttpServerOptions().setHost("\<Host\>").setPort(\<Port\>)</code>)
     */
    public WidgetBridge(HttpServerOptions httpOptions) {
        this.httpOptions = httpOptions;
        this.sockJSBridgeOptions = this.defaultSockJSBridgeOptions;
    }

    /**
     * This constructor supplies default options.
     *
     * @param httpOptions the HttpServerOptions to create the HTTP-Server (e.g., <code>new HttpServerOptions().setHost("\<Host\>").setPort(\<Port\>)</code>)
     * @param sockJSBridgeOptions the SockJSBridgeOptions to handle rules for the EventBusBridge
     */
    public WidgetBridge(HttpServerOptions httpOptions,
                        SockJSBridgeOptions sockJSBridgeOptions) {
        this.httpOptions = httpOptions;
        this.sockJSBridgeOptions = sockJSBridgeOptions;
    }

    /**
     * Default constructor. Creates HttpServerOptions with localhost and port 8080
     * and uses the defaultSockJSBridgeOptions for the applied rules.
     * TODO: Other default host and port may be specified in a config file.
     */
    public WidgetBridge() {
        this(new HttpServerOptions()
            .setHost("localhost").setPort(8080));
    }

    /**
     * Creates a sockJSHandler using vertx.
     *
     * @return Router to be mounted on an existing Router bridging the eventBus with the defined sockJSBridgeOptions
     */
    private Router bridgeHandler() {
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        return sockJSHandler.bridge(this.sockJSBridgeOptions);
    }

    /**
     * Creates a staticHandler for serving static resources from the file system or classpath.
     * May be used to display HTML-Page explaining how to use the WidgetBridge
     * or to redirect users to the right url: {@link "http://localhost:8080"}
     *
     * @return StaticHandler
     */
    private StaticHandler staticHandler() {
        return StaticHandler.create()
                .setCachingEnabled(false);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.mountSubRouter("/bridge", bridgeHandler());
        router.route().handler(staticHandler());

        HttpServer http = vertx.createHttpServer(this.httpOptions)
                .requestHandler(router)
                .listen();

        logger.info("Server listening on {}:{}/bridge", this.httpOptions.getHost(), this.httpOptions.getPort());
        startPromise.complete();
    }
}