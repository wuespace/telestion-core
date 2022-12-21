package de.wuespace.telestion.api;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

public record MockMessage<T>(
		String address,
		String replyAddress,
		MultiMap headers,
		T body,
		boolean isSend,
		ReplyHandler replyHandler,
		boolean succeedOnRequest
) implements Message<T> {

	public MockMessage(String address, MultiMap headers, T body, boolean isSend) {
		this(address, null, headers, body, isSend, ReplyHandler.emptyHandler(), false);
	}

	public MockMessage(String address, MultiMap header, T body) {
		this(address, header, body, false);
	}

	public MockMessage(String address, T body) {
		this(address, MultiMap.caseInsensitiveMultiMap(), body);
	}

	@Override
	public void reply(Object message, DeliveryOptions options) {
		replyHandler.handle(message, options);
	}

	@Override
	public <R> Future<Message<R>> replyAndRequest(Object message, DeliveryOptions options) {
		reply(message, options);
		return succeedOnRequest ? Future.succeededFuture() : Future.failedFuture("Intended failure");
	}
}
