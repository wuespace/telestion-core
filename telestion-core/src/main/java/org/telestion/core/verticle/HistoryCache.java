package org.telestion.core.verticle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.Position;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * HistoryCache is a verticle which stores the last received messages of a specified type.
 * <p>
 *  You have to configure the following options:
 * <ul>
 *  <li>messageName - the name of the message type (e.g., <code>Position.class.getSimpleName()</code>)</li>
 *  <li>addressName - the address to which the message is send</li>
 *  <li>historySize - the maximum size of the history</li>
 * </ul>
 * </p>
 * <p>
 *  To fetch the last elements, you have to send a {@link Request} to <code>HistoryCache.class.getSimpleName()</code>.
 *  The last elements will be returned in a {@link Response} message.
 * </p>
 * <p>
 *  An example looks like this:
 * <pre>
 * {@code
 *   vertx.eventBus().request(Address.incoming(HistoryCache.class), new Request(Position.class, 10), msgResult -> {
 *       if(msgResult.failed()) return;
 *       if(msgResult.result().body() instanceof Response response){
 *           var list = response.as(Position.class);
 *       }
 *   });
 * }
 * </pre>
 * </p>
 */
public final class HistoryCache extends AbstractVerticle {

    private final String messageName;
    private final String addressName;
    private final int historySize;

    /**
     * This constructor supplies default options.
     *
     * @param messageName the name of the message type (e.g., <code>Position.class.getSimpleName()</code>)
     * @param addressName the address to which the message is send
     * @param historySize the maximum size of the history
     */
    public HistoryCache(String messageName, String addressName, int historySize) {
        this.messageName = messageName;
        this.addressName = addressName;
        this.historySize = historySize;
    }

    /**
     * This constructor supplies default options.
     *
     * @param messageClass the name of the message type (e.g., <code>Position.class.getSimpleName()</code>)
     * @param source the address to which the message is send
     * @param historySize the maximum size of the history
     */
    public HistoryCache(Class<? extends JsonMessage> messageClass, Class<? extends Verticle> source, int historySize) {
        //TODO swap oder of arguments
        //TODO think about an exposing static method? Which exposes public MessageTypes?
        this.messageName = messageClass.getName();
        this.addressName = Address.outgoing(source);
        this.historySize = historySize;
    }

    /**
     * No default options are supplied.
     * This means they have to be specified as {@link io.vertx.core.DeploymentOptions}.
     */
    public HistoryCache(){
        this((String)null, (String)null, 0);
    }

    /**
     * A request which asks for the last elements.
     * @param maxSize the maximum number of elements in the response. Only the number of elements in the history will be
     *                returned if this is higher than the number of elements in the history. At most, this is the number
     *                of elements are specified in the HistoryCache configuration.
     * @param messageName the name of the message which should be returned (e.g., <code>Position.class.getSimpleName()</code>).
     */
    public static record Request(@JsonProperty String messageName, @JsonProperty int maxSize) implements JsonMessage {
        private Request(){
            this((String)null, 0);
        }

        /**
         *
         * @param messageType the type of the requested message
         * @param maxSize the maximum number of elements in the response. If the number of elements is higher than the number of elements in the history,
         *                only the number of elements in the history will be returned. This is, at most, the number
         *                of elements specified in the HistoryCache configuration.
         */
        public Request(Class<? extends JsonMessage> messageType, int maxSize){
            this(messageType.getName(), maxSize);
        }
    }

    /**
     * The response containing a list of the latest received messages.
     * @param history the history
     */
    public static record Response/*<T extends JsonMessage>*/(@JsonProperty List<Position> history) implements JsonMessage {
        private Response(){
            this(null);
        }

        /**
         * Get a casted version of the response.
         *
         * @param type the wanted type class. This should be equal to the request.
         * @return the casted list
         */
        //@SuppressWarnings("unchecked")
        //public T[] as(Class<T> type){
        //    return history();
        //}
    }

    @Override
    public void start(Promise<Void> startPromise) throws ClassNotFoundException {
        var messageName = Objects.requireNonNull(context.config().getString("messageName", this.messageName));
        var messageClass = Class.forName(messageName);
        var addressName = Objects.requireNonNull(context.config().getString("addressName", this.addressName));
        var historySize = Objects.requireNonNull(context.config().getInteger("historySize", this.historySize));

        var history = new JsonMessage[historySize];
        var idx = new int[]{0};
        vertx.eventBus().consumer(addressName, msg -> {
            JsonMessage.on((Class<? extends JsonMessage>)messageClass, msg, jsonMessage -> {
                vertx.sharedData().getLocalLock("historyLock", lockResult -> {
                    if(lockResult.failed()){
                        throw new RuntimeException();
                    }
                    history[idx[0]++] = jsonMessage;
                    lockResult.result().release();
                });
            });
        });
        vertx.eventBus().consumer(Address.incoming(HistoryCache.class), msg -> {
            JsonMessage.on(Request.class, msg, req -> {
                var maxSize = req.maxSize();
                var size = Math.min(maxSize, historySize);
                vertx.sharedData().getLocalLock("historyLock", lockResult -> {
                    if(lockResult.failed()){
                        throw new RuntimeException();
                    }
                    var response = IntStream.range(0, size)
                            .map(i -> (historySize+idx[0]-1-i)%historySize)
                            .mapToObj(i -> (Position)history[i])
                            .filter(Objects::nonNull).collect(Collectors.toList());
                    lockResult.result().release();
                    msg.reply(new Response(response).json());
                });
            });
        });
        startPromise.complete();
    }


    /**
     * TODO refactor out in example class
     *
     * A small self containing example
     * It listens to a started {@link PositionPublisher} and keeps a history.
     *
     * @param args
     */
    @Deprecated
    public static void main(String[] args) throws InterruptedException {
        var vertx = Vertx.vertx();
        var cache = new HistoryCache(Position.class, PositionPublisher.class, 50);
        vertx.deployVerticle(cache);
        vertx.deployVerticle(new PositionPublisher());
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        vertx.eventBus().request(Address.incoming(HistoryCache.class), new Request(Position.class, 3).json(), msgResult -> {
            JsonMessage.on(Response.class, msgResult.result(), resp -> {
                System.out.println(resp.history()); //should be 2 elements because publishing rate is once every two secs
                vertx.close();
            });
        });
    }
}
