package org.telestion.core.verticle;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

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

    //public static final class Config

    private final Logger logger = LoggerFactory.getLogger(WidgetBridge.class);
    private String host;
    private Integer port;
    private List<String> inboundPermitted = Collections.emptyList();
    private List<String> outboundPermitted = Collections.emptyList();

    /**
     * This constructor supplies default options
     * and uses the defaultSockJSBridgeOptions for the applied rules.
     *
     * @param host the ip address of the host on which the HTTP-Server should run
     * @param port the port on which the HTTP-Server should listen
     * @param inboundPermitted the permitted eventbus addresses for inbound connections
     * @param outboundPermitted the permitted eventbus addresses for outbound connections
     */
    public WidgetBridge(String host, int port, List<String> inboundPermitted, List<String> outboundPermitted) {
        this.host = host;
        this.port = port;
        this.inboundPermitted = inboundPermitted;
        this.outboundPermitted = outboundPermitted;
    }

    /**
     * If this constructor is used all settings have to be specified in the config file
     */
    public WidgetBridge() { }

    @Override
    public void start(Promise<Void> startPromise) {
        host = Objects.requireNonNull(context.config().getString("host", host));
        port = Objects.requireNonNull(context.config().getInteger("port", port));
        if(context.config().getJsonArray("inboundPermitted") != null){
            inboundPermitted = context.config().getJsonArray("inboundPermitted")
                    .stream().map(addr -> (String)addr).collect(Collectors.toList());
        }
        if(context.config().getJsonArray("outboundPermitted") != null) {
            outboundPermitted = context.config().getJsonArray("outboundPermitted")
                    .stream().map(addr -> (String) addr).collect(Collectors.toList());
        }
        
        HttpServerOptions httpOptions = new HttpServerOptions()
                .setHost(host)
                .setPort(port);

        Router router = Router.router(vertx);

        router.mountSubRouter("/bridge", bridgeHandler());
        router.route().handler(staticHandler());

        vertx.createHttpServer(httpOptions)
                .requestHandler(router)
                .listen();

        logger.info("Server listening on {}:{}/bridge", httpOptions.getHost(), httpOptions.getPort());
        startPromise.complete();
    }

    /**
     * Creates a sockJSHandler using vertx.
     *
     * @return Router to be mounted on an existing Router bridging the eventBus with the defined sockJSBridgeOptions
     */
    private Router bridgeHandler() {
        logger.info("Inbound permitted: "+inboundPermitted);
        logger.info("Outbound permitted: "+outboundPermitted);
        SockJSBridgeOptions sockJSBridgeOptions = new SockJSBridgeOptions();
        inboundPermitted.forEach(addr -> sockJSBridgeOptions.addInboundPermitted(new PermittedOptions().setAddress(addr)));
        outboundPermitted.forEach(addr -> sockJSBridgeOptions.addOutboundPermitted(new PermittedOptions().setAddress(addr)));

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        return sockJSHandler.bridge(sockJSBridgeOptions);
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
}