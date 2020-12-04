package org.telestion.core.database;

import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.DataOperation;
import org.telestion.core.message.DataRequest;
import org.telestion.core.monitoring.MessageLogger;

public class DataRequestService extends AbstractVerticle implements IDataRequestService {
	private final Logger logger = LoggerFactory.getLogger(DataRequestService.class);

	private final String inSave = Address.incoming(DataRequestService.class, "save");
	private final String outSave = Address.outgoing(DataRequestService.class, "save");
	private final String inFind = Address.incoming(DataRequestService.class, "find");
	private final String outFind = Address.outgoing(DataRequestService.class, "find");

	private final String dbFind = Address.incoming(MongoDatabase.class, "findLatest");
	private final String dbFindResponse = Address.outgoing(MongoDatabase.class, "findLatest");

	private final String opIntegrate = "AddressToIntegrateOp";

	public DataRequestService() {}

	public static void main (String[] args) {
		Vertx vertx = Vertx.vertx();
		// deploy verticles
		vertx.deployVerticle(new DataRequestService());
		vertx.deployVerticle(new MessageLogger());
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		this.registerConsumers();
		startPromise.complete();
	}

	private void registerConsumers() {
		vertx.eventBus().consumer(inFind, request -> {
			JsonMessage.on(DataRequest.class, request, req -> {
				this.dataRequestDispatcher(req, res -> {
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

	@Override
	public void dataRequestDispatcher(DataRequest request, Handler<AsyncResult<JsonObject>> resultHandler) {
		if (request.dataTypes().size() == 1) {
			if (request.operation().isPresent()) {
				DataOperation op = new DataOperation(request.operation().get().operationAddress(), new JsonObject(), request.operation().get().params());
				this.fetchLatestData(request.dataTypes().get(0), res -> {
					if (res.failed()) {
						resultHandler.handle(Future.failedFuture(res.cause().getMessage()));
						return;
					}
					op.data().put("data", res.result());
				}).applyManipulation(op, resultHandler);
			} else {
				this.fetchLatestData(request.dataTypes().get(0), res -> {
					if (res.failed()) {
						resultHandler.handle(Future.failedFuture(res.cause().getMessage()));
						return;
					}
					resultHandler.handle(Future.succeededFuture(res.result()));
				});
			}
		} else {
			this.fetchLatestData(request.dataTypes(), res -> {
				if (res.failed()) {
					resultHandler.handle(Future.failedFuture(res.cause().getMessage()));
					return;
				}
				JsonObject wrapped = new JsonObject().put("data", res.result());
				resultHandler.handle(Future.succeededFuture(wrapped));
			});
		}
	}

	@Override
	public IDataRequestService fetchLatestData(List<Class<?>> dataTypes, Handler<AsyncResult<JsonArray>> resultHandler) {
		JsonArray result = new JsonArray();
		dataTypes.forEach(dataType -> {
			vertx.eventBus().request(dbFind, dataType, reply -> {
				if (reply.failed()) {
					logger.error(reply.cause().getMessage());
					resultHandler.handle(Future.failedFuture(reply.cause().getMessage()));
					return;
				}
				JsonObject dataRes = new JsonObject();
				result.add(dataRes.put("data", reply.result().body()));
			});
		});
		resultHandler.handle(Future.succeededFuture(result));
		return this;
	}

	@Override
	public IDataRequestService fetchLatestData(Class<?> dataType, Handler<AsyncResult<JsonObject>> resultHandler) {
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
		return this;
	}

	@Override
	public IDataRequestService applyManipulation(DataOperation dataOperation, Handler<AsyncResult<JsonObject>> resultHandler) {
		JsonObject result = new JsonObject();
		vertx.eventBus().request(dataOperation.operationAddress(), dataOperation, reply -> {
			if (reply.failed()) {
				logger.error(reply.cause().getMessage());
				resultHandler.handle(Future.failedFuture(reply.cause().getMessage()));
				return;
			}
			result.put("data", reply.result().body());
			resultHandler.handle(Future.succeededFuture(result));
		});
		return this;
	}
}
