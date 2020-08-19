package org.telestion.core.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;
import org.telestion.core.message.Position;

public class MessageSafer extends AbstractVerticle {

    String addr;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("addr1", msg -> {
            if(msg.body() instanceof Position pos){

                var stringPos = JsonCodec.INSTANCE.toString(pos);
            }
        });
        startPromise.complete();
    }
}
