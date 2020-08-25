package org.telestion.safer.mavlink;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.core.message.Position;

/**
 * TODO: Add documentation
 */
public class MessageSafer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MessageSafer.class);
    private static  FileHandler file;
    private String addr = "";


    public MessageSafer(String listeningAddress, String backupFileName){
        this.addr = listeningAddress;
        file = new FileHandler(backupFileName);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer(addr, msg -> {
            logger.info("received message: " + msg.body().toString());
            file.save(msg.body().toString());
            /*
            FOR THE FINAL IMPLEMENTATION
            if(msg.body() instanceof Position pos){
                var stringPos = JsonCodec.INSTANCE.toString(pos);
            }*/
        });
        startPromise.complete();
    }
}
