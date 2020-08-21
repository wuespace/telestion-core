package org.telestion.core.verticle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.telestion.api.message.JsonMessage;

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
 *   vertx.eventBus().request(HistoryCache.class.getSimpleName(), new Request(Position.class, 10), msgResult -> {
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

    /**
     * A request which asks for the last elements.
     * @param maxSize the maximum number of elements in the response. Only the number of elements in the history will be
     *                returned if this is higher than the number of elements in the history. At most, this is the number
     *                of elements are specified in the HistoryCache configuration.
     * @param messageName the name of the message which should be returned (e.g., <code>Position.class.getSimpleName()</code>).
     */
    public record Request(@JsonProperty String messageName, @JsonProperty int maxSize) implements JsonMessage {
        private Request(){
            this((String)null, 0);
        }

        /**
         *
         * @param messageType the type of the requested message
         * @param maxSize the maximum number of elements in the response. Only the number of elements in the history will be
         *                returned if this is higher than the number of elements in the history. This are at most the number
         *                of elements specified in the HistoryCache configuration.
         */
        public Request(Class<? extends JsonMessage> messageType, int maxSize){
            this(messageType.getSimpleName(), maxSize);
        }
    }

    /**
     * The response containing a list of the last received messages.
     * @param history the history
     */
    public record Response(@JsonProperty List<? extends JsonMessage> history) implements JsonMessage {
        private Response(){
            this(null);
        }

        /**
         * Get a casted version of the response.
         *
         * @param type the wanted type class. This should be equal to the request.
         * @param <T> the wanted type
         * @return the casted list
         */
        @SuppressWarnings("unchecked")
        public <T extends JsonMessage> List<T> as(Class<T> type){
            return (List<T>) history();
        }
    }

    @Override
    public void start(Promise<Void> startPromise) {
        var messageName = Objects.requireNonNull(context.config().getString("messageName"));
        var addressName = Objects.requireNonNull(context.config().getString("addressName"));
        var historySize = Objects.requireNonNull(context.config().getInteger("historySize"));

        var history = new JsonMessage[historySize];
        var idx = new int[]{0};
        vertx.eventBus().consumer(addressName, msg -> {
           if(msg.body() instanceof JsonMessage jsonMessage && messageName.equals(jsonMessage.name())){
                vertx.sharedData().getLocalLock("historyLock", lockResult -> {
                    if(lockResult.failed()){
                        throw new RuntimeException();
                    }
                    history[idx[0]++] = jsonMessage;
                    lockResult.result().release();
                });
           }
        });
        vertx.eventBus().consumer(HistoryCache.class.getSimpleName(), msg -> {
            if(msg.body() instanceof Request req && messageName.equals(req.messageName())){
                var maxSize = req.maxSize();
                var size = Math.min(maxSize, historySize);
                vertx.sharedData().getLocalLock("historyLock", lockResult -> {
                    if(lockResult.failed()){
                        throw new RuntimeException();
                    }
                    var response = IntStream.range(0, size)
                            .map(i -> (historySize+idx[0]-1-i)%historySize)
                            .mapToObj(i -> history[i])
                            .filter(Objects::nonNull).collect(Collectors.toList());
                    lockResult.result().release();
                    msg.reply(new Response(response));
                });
            }
        });
        startPromise.complete();
    }
}
