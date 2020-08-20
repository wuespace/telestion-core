package org.telestion.core.verticle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.telestion.api.message.JsonMessage;

import java.util.ArrayList;
import java.util.List;

public final class HistoryCache extends AbstractVerticle {

    record Request(@JsonProperty String messageName, @JsonProperty int maxSize) implements JsonMessage {
        private Request(){
            this(null, 0);
        }
    }

    record Response(@JsonProperty List<JsonMessage> history) implements JsonMessage {
        private Response(){
            this(null);
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
