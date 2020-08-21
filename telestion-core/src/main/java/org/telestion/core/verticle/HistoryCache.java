package org.telestion.core.verticle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.telestion.api.message.JsonMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryCache is a verticle which stores the last received messages of a specified type.
 * <p>
 *  You have to configure the following options:
 * <ul>
 *  <li>messageName - is the name of the message type (eg. Position.class.getSimpleName()).</li>
 *  <li>addressName - is the address to which the message is send.</li>
 *  <li>historySize - is the maximum size of the history</li>
 * </ul>
 * </p>
 * <p>
 *  To fetch the last elements you have to send a {@link Request} to <code>HistoryCache.class.getSimpleName()</code>.
 *  The last elements will be returned in a {@link Response} message.
 * </p>
 * <p>
 *  A simple example looks like this:
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
     *                returned if this is higher than the number of elements in the history. This are at most the number
     *                of elements specified in the HistoryCache configuration.
     * @param messageName the name of the messages which should be returned (eg. Position.class.getSimpleName()).
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
    public void start(Promise<Void> startPromise) throws Exception {
        var messageName = context.config().getString("messageName");
        // every JsonMessage.nmae() has to be unique
        // TODO put this as requirement which is asserted during runtime with an AssertionVerticle which observes the eventbus
        var addressName = context.config().getString("addressName");
        var historySize = context.config().getInteger("historySize");
        vertx.eventBus().consumer(addressName, msg -> {
           if(msg.body() instanceof JsonMessage jsonMessage && jsonMessage.name().equals(messageName)){
               //TODO store data ...
           }
        });
        vertx.eventBus().consumer(HistoryCache.class.getSimpleName(), msg -> {
            if(msg.body() instanceof Request req && req.messageName().equals(messageName)){
                var maxSze = req.maxSize();
                var response = new ArrayList<JsonMessage>();
                //TODO fill list ...
                msg.reply(new Response(response));
            }
        });
        startPromise.complete();
    }
}
