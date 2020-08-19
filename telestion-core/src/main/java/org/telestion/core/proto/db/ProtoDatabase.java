package org.telestion.core.proto.db;

import com.hazelcast.internal.json.Json;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.spi.json.JsonCodec;
import org.telestion.core.message.Position;
import org.telestion.core.message.PositionList;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A database which stores positions.
 * It stores {@link Position} which are send to "mavlink".
 * On a request at "request#position" it answers with a {@link java.util.List}
 * of the last 100 received {@link Position}.
 */
public final class ProtoDatabase extends AbstractVerticle {

    private static final int HistorySize = 100;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("mavlink", msg -> {
            if(msg.body() instanceof Position position){
                var idx = (int)vertx.sharedData().getLocalMap("position").getOrDefault("next", 0);
                vertx.sharedData().getLocalMap("position").put(idx, JsonCodec.INSTANCE.toString(position));
                vertx.sharedData().getLocalMap("position").put("next", ((idx+1)%HistorySize));
            }
        });
        vertx.eventBus().consumer("request#position", msg -> {
            var idx = (int)vertx.sharedData().getLocalMap("position").getOrDefault("next", 0);
            var result = IntStream.range(0, HistorySize)
                    .map(i -> (HistorySize+idx-1-i)%HistorySize)
                    .boxed()
                    .filter(vertx.sharedData().getLocalMap("position")::containsKey)
                    .map(i -> JsonCodec.INSTANCE.fromString((String)vertx.sharedData().getLocalMap("position").get(i), Position.class))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            msg.reply(JsonCodec.INSTANCE.toString(new PositionList(result)));
        });
        startPromise.complete();
    }
}
