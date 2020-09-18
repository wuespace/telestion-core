package org.telestion.core.proto.db;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.telestion.api.message.JsonMessage;
import org.telestion.example.Position;
import org.telestion.example.Positions;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A database which stores positions.
 * It stores {@link Position} which are sent to "mavlink".
 * Upon request at "request#position" it answers with a {@link java.util.List}
 * of the last 100 received {@link Position}.
 */
public final class ProtoDatabase extends AbstractVerticle {

    private static final int HistorySize = 100;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("current-position", msg -> {
            if(msg.body() instanceof Position position){
                var idx = (int)vertx.sharedData().getLocalMap("position").getOrDefault("next", 0);
                vertx.sharedData().getLocalMap("position").put(idx, position.json());
                vertx.sharedData().getLocalMap("position").put("next", ((idx+1)%HistorySize));
            }
        });
        vertx.eventBus().consumer("request#position", msg -> {
            var idx = (int)vertx.sharedData().getLocalMap("position").getOrDefault("next", 0);
            var result = IntStream.range(0, HistorySize)
                    .map(i -> (HistorySize+idx-1-i)%HistorySize)
                    .boxed()
                    .filter(vertx.sharedData().getLocalMap("position")::containsKey)
                    .map(i -> JsonMessage.from(vertx.sharedData().getLocalMap("position").get(i), Position.class))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            msg.reply(new Positions(result).json());
        });
        startPromise.complete();
    }
}
