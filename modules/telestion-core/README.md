## Event Bus Widget Bridge

#### Simple Explanation
The WidgetBridge uses WebSockets to extend
the `vertx.eventBus()` to an HTTP-Server.

We create a `Router` using vertx. This
Router handles HTTP-Requests to the HTTP-Server.

After that we mount a SubRouter
 `bridgeHandler()` to our created Router.
 This handler defines which options we use
 for our SockJS-WebSocket-Connection.
 
 In the `SockJSBridgeOptions` we define
 `PermittedOptions`.
 The EventBusBridge by default is deny-any,
 which means that any message which is not
 permitted explicitly will be denied
 (except reply messages).
 
 If you want to send messages
 to the frontend you need to register
 `PermittedOptions` with 
 `addOutboundPermitted(...)`.
 You can allow complete addresses, 
 regex-expressions of addresses and
 JsonObject-Matches. See the documentation
 of `PermittedOptions` for further explanation.
 
 After that a `SockJSHandler` is created
 and returned with `bridge(options)`.
 
 When bridging you can listen for special
 `event.type()`s, e.g.
 `BridgeEventType.SOCKET_CREATED` to listen
 if a client connected to the EventBus.
 If you do that is important to finish
 the callback with `event.complete(true)`.
 
 ##### Code Snippets
 ######BridgeOptions with event handling
 ```
return sockJSHandler.bridge(options, event -> {
if (event.type() == BridgeEventType.SOCKET_CREATED) {
logger.info("A socket was created.");
}
if (event.type() == BridgeEventType.SEND) {
logger.info("Client sent message.");
}
System.out.println(event.type());
event.complete(true);
// You have to complete(true) to make
// clear that the event has finished.
// If you for example want to reject
// certain event types you can complete(false).
});
```

######Publish to the frontend
To publish messages to the frontend you
simply publish to the event bus in
your verticle like you normally would.
```
vertx.eventBus().publish(
Address.outgoing(
<YourClass>.class, "method"
), 
data.json()
);
```
Then you have to allow that address to
go through to the frontend.
```
.addOutboundPermitted(new PermittedOptions()
.setAddress(Address.outgoing(
<YourClass>.class, "method")))
```
######Receive from frontend
To receive messages you simply do the same
as publishing, but you use
```
vertx.eventBus().consumer(<Address>, msg -> {
JsonMessage.on(<YourClass>.class, msg, data -> {
System.out.println(data);
});
});
```
and `addInboundPermitted(...)`.

##### Receive in frontend

The HTTP-Server to consume the
EventBus listens on
`http://<IP-Address>:<Port>/bridge`.
