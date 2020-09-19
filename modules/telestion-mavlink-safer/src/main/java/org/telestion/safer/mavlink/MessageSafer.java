package org.telestion.safer.mavlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.spi.json.JsonCodec;
import org.telestion.example.Position;

/**
 * Entry point for Matei. Here you could start :)
 */
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
