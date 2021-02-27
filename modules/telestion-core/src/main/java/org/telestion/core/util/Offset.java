package org.telestion.core.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.DataOperation;

public final class Offset extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(Offset.class);

	private final String inOffset = Address.incoming(Offset.class);

	public Offset() {}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		this.registerConsumers();
		startPromise.complete();
	}

	private void registerConsumers() {
		vertx.eventBus().consumer(inOffset, request -> {
			JsonMessage.on(DataOperation.class, request, dataOperation -> {
				this.calculateOffset(dataOperation, res -> {
					if (res.failed()) {
						request.fail(-1, res.cause().getMessage());
					} else {
						request.reply(res.result());
					}
				});
			});
		});
	}

	private void calculateOffset(DataOperation dataOperation, Handler<AsyncResult<JsonObject>> handler) {
		// TODO: parse data of dOp to numeric value
		// TODO: parse offset of dOp to numeric value
		// TODO: add offset to data and return
	}
}
