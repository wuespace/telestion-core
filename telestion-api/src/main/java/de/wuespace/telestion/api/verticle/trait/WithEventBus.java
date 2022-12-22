package de.wuespace.telestion.api.verticle.trait;

import de.wuespace.telestion.api.message.HeaderInformation;
import de.wuespace.telestion.api.message.JsonRecord;
import io.vertx.core.MultiMap;
import io.vertx.core.Verticle;

/**
 * Allows {@link Verticle} instances to get simplified access to the Vert.x event bus.
 * These traits support automatic conversion of different message types like {@link JsonRecord}
 * and automatic attachment of {@link MultiMap} or {@link HeaderInformation} to sent messages on the event bus.
 *
 * <h2>Usage</h2>
 * <pre>
 * {@code
 * public class MyVerticle extends TelestionVerticle implements WithEventBus {
 *     @Override
 *     public void onStart() {
 *         register("channel-1", this::handle, Position.class);
 *     }
 *
 *     private void handle(Position position) {
 *         logger.info("Current position: {}, {}", position.x, position.y);
 *     }
 * }
 * }
 * </pre>
 *
 * @author Pablo Klaschka (@pklaschka), Ludwig Richter (@fussel178)
 */
public interface WithEventBus extends Verticle, WithEventBusPublish, WithEventBusSend, WithEventBusRequest,
        WithEventBusRegister {
}
