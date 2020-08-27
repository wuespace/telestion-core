package org.telestion.adapter.mavlink;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.adapter.mavlink.message.internal.AddressMapping;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * TODO: Java-Docs to make @pklaschka happy ;)
 * 
 * @author Cedric Boes
 * @version 1.0
 */
public final class AddressAssociator extends AbstractVerticle {

	/**
	 * 
	 */
	private final HashMap<String, String> mapping = new HashMap<>();
	
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(AddressAssociator.class);
	
	/**
	 * 
	 */
	public static final String putAddress = Address.incoming(AddressAssociator.class, "put");
	/**
	 * 
	 */
	public static final String removeAddress = Address.incoming(AddressAssociator.class, "remove");
	/**
	 * 
	 */
	public static final String outAddress = Address.outgoing(AddressAssociator.class);

	@SuppressWarnings("preview")
	@Override
	public void start(Promise<Void> startPromise) {
		vertx.eventBus().consumer(putAddress, msg -> {
			if (JsonMessage.on(AddressMapping.class, msg, map -> {
				mapping.put(map.mavAddress(), map.ip());
				return;
			}));
			else {
				logger.warn("Unsupported message received on {}!", msg.address());
			}
		});
		
		vertx.eventBus().consumer(removeAddress, msg -> {
			if (msg.body() instanceof String s) {
				vertx.eventBus().publish(outAddress, mapping.remove(s));
			} else {
				logger.warn("Unsupported message received on {}!", msg.address());
			}
		});
		
		startPromise.complete();
	}
	
	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}
}
