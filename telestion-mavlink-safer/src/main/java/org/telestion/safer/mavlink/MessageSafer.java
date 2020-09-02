package org.telestion.safer.mavlink;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.Position;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * A class that listens to the {@link Vertx Vertx} eventBus connection with {@link org.telestion.core.verticle.PositionPublisher} and sends the json-encoded {@link Position} objects to {@link FileHandler} to continue with the backup process.
 *
 * @version 1.0
 * @author Matei Oana
 */
public class MessageSafer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MessageSafer.class);
    private static  FileHandler file;

    public MessageSafer(){
        file = new FileHandler();
    }

    /**
     * Sets a consumer {@link Verticle Verticle} to listen to the eventBus and handles the message to the save method of the {@link FileHandler} class. </br>
     */
    // TODO The listening address is just for test purposes and should be changed prior to release
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer(Address.outgoing(RandomPositionPublisher.class, "MockPos"), msg -> {
            JsonMessage.on(Position.class, msg.body(), data->{
                logger.info("Position object received: " + msg.body());
                file.save(msg.body().toString());
            });
        });
        startPromise.complete();
    }
}
