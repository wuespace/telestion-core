package de.wuespace.telestion.api;

import io.vertx.core.eventbus.DeliveryOptions;

@FunctionalInterface
public interface ReplyHandler {
	static ReplyHandler emptyHandler() {
		return (message, options) -> {};
	}

	void handle(Object message, DeliveryOptions options);
}
