package org.telestion.core.database;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.DbRequest;
import org.telestion.core.monitoring.MessageLogger;

public class DataRequestService extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(DataRequestService.class);
	private final String inSave = Address.incoming(DataRequestService.class, "save");
	private final String outSave = Address.outgoing(DataRequestService.class, "save");
	private final String inFind = Address.incoming(DataRequestService.class, "find");
	private final String outFind = Address.outgoing(DataRequestService.class, "find");
	private final String dbFind = Address.incoming(MongoDatabaseService.class, "findLatest");
	private final String dbFindResponse = Address.outgoing(MongoDatabaseService.class, "findLatest");
	private final String opIntegrate = "AddressToIntegrateOp";

	public DataRequestService() {}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		// deploy verticles
		vertx.deployVerticle(new DataRequestService());
		vertx.deployVerticle(new MessageLogger());
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

	}

	private void registerConsumers() {
		vertx.eventBus().consumer(inFind, request -> {
			JsonMessage.on(DbRequest.class, request, req -> {
				this.fetchLatestData(req.dataType(), res -> {
					if (res.failed()) {
						logger.error(res.cause().getMessage());
						request.fail(-1, res.cause().getMessage());
						return;
					}
					logger.info(res.result().toString());
					request.reply(res.result());
				});
			});
		});
	}

	public void fetchLatestData(Class<?> dataType, Handler<AsyncResult<JsonObject>> resultHandler) {
		JsonObject result = new JsonObject();
		vertx.eventBus().request(dbFind, dataType, reply -> {
			if (reply.failed()) {
				logger.error(reply.cause().getMessage());
				resultHandler.handle(Future.failedFuture(reply.cause().getMessage()));
				return;
			}
			result.put("data", reply.result().body());
			resultHandler.handle(Future.succeededFuture(result));
		});
	}
}
