package org.telestion.core.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.api.config.Config;
import org.telestion.core.message.Address;
import org.telestion.core.message.DataOperation;
import org.telestion.core.message.DataRequest;
import org.telestion.core.message.DbRequest;

/**
 * DataService is a verticle which is the interface to a underlying database implementation.
 * All data requests should come to the DataService and will be parsed and executed.
 * TODO: Save data with the DataService.  Right now the save command is handled by the db directly.
 * TODO: DataService listens for publishes of new data from UART / Mavlink / ...
 * TODO: DataOperations like Integrate, Differentiate, Offset, Sum, ...
 * TODO: Change dataTypeMap to Class.forName(...) and get the class name string from frontend.
 * TODO: MongoDB Queries explanation and implementation in fetchLatestData.
 *
 */
public final class DataService extends AbstractVerticle {
	private final Configuration forcedConfig;
	private Configuration config;

	private final Logger logger = LoggerFactory.getLogger(DataService.class);
	/**
	 * DataService Eventbus Addresses.
	 */
	private final String inSave = Address.incoming(DataService.class, "save");
	private final String inFind = Address.incoming(DataService.class, "find");
	private final String dbSave = Address.incoming(MongoDatabaseService.class, "save");
	private final String dbFind = Address.incoming(MongoDatabaseService.class, "find");

	/**
	 * If this constructor is used, settings have to be specified in the config file.
	 */
	public DataService() {
		this.forcedConfig = null;
	}

	/**
	 * This constructor supplies default options.
	 *
	 * @param dataTypeMap			Map of String->Class<?> for incoming dataRequests
	 * @param dataOperationMap		Map of String->DataOperation for incoming dataRequests
	 */
	public DataService(Map<String, Class<?>> dataTypeMap, Map<String, DataOperation> dataOperationMap) {
		this.forcedConfig = new Configuration(dataTypeMap, dataOperationMap);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(forcedConfig, config(), Configuration.class);
		this.registerConsumers();
		startPromise.complete();
	}

	/**
	 * Method to register consumers to the eventbus.
	 */
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
		vertx.eventBus().consumer(inSave, document -> {
			vertx.eventBus().request(dbSave, document, res -> {
				if (res.failed()) {
					logger.error(res.cause().getMessage());
					document.fail(-1, res.cause().getMessage());
					return;
				}
				logger.info(res.result().toString());
				document.reply(res.result().body());
			});
		});
	}

	/**
	 * Parse and dispatch incoming DataRequests.
	 *
	 * @param request			Determines which dataTypes should be retrieved and if an Operation should be executed.
	 * @param resultHandler		Handles the request to the underlying database. Can be failed or succeeded.
	 */
	private void dataRequestDispatcher(DataRequest request, Handler<AsyncResult<JsonObject>> resultHandler) {
		var dataTypes = new ArrayList<Class<?>>();
		request.classNames().forEach(clazz -> {
			if (config.dataTypeMap.get(clazz) != null) {
				dataTypes.add(config.dataTypeMap.get(clazz));
			}
		});
		if (dataTypes.size() == 1) {
			if (!request.operation().equals("")) {
				logger.info("Operations are not yet supported!");
				/* TODO:
				DataOperation dOp = new DataOperation(request.operation().get().operationAddress(),
									new JsonObject(), request.operation().get().params());
				this.fetchLatestData(request.dataTypes().get(0), res -> {
					if (res.failed()) {
						resultHandler.handle(Future.failedFuture(res.cause().getMessage()));
						return;
					}
					dOp.data().put("data", res.result());
				}).applyManipulation(dOp, resultHandler);*/
			} else {
				this.fetchLatestData(dataTypes.get(0), res -> {
					if (res.failed()) {
						resultHandler.handle(Future.failedFuture(res.cause().getMessage()));
						return;
					}
					resultHandler.handle(Future.succeededFuture(res.result()));
				});
			}
		} else {
			this.fetchLatestData(dataTypes, res -> {
				if (res.failed()) {
					resultHandler.handle(Future.failedFuture(res.cause().getMessage()));
					return;
				}
				JsonObject wrapped = new JsonObject().put("data", res.result());
				resultHandler.handle(Future.succeededFuture(wrapped));
			});
		}
	}

	/**
	 * Method to fetch the latest data of a specified data types.
	 *
	 * @param dataTypes			Determines which data types should be fetched.
	 * @param resultHandler		Handles the request to the underlying database. Can be failed or succeeded.
	 * @return the data service to chain operations.
	 */
	private DataService fetchLatestData(List<Class<?>> dataTypes,
										Handler<AsyncResult<JsonArray>> resultHandler) {
		JsonArray result = new JsonArray();
		dataTypes.forEach(dataType -> {
			DbRequest dbRequest = new DbRequest(dataType, new JsonObject());
			vertx.eventBus().request(dbFind, dbRequest, reply -> {
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

	/**
	 * Method to fetch the latest data of a specified data type.
	 *
	 * @param dataType			Determines which data type should be fetched.
	 * @param resultHandler		Handles the request to the underlying database. Can be failed or succeeded.
	 * @return the data service to chain operations.
	 */
	private DataService fetchLatestData(Class<?> dataType, Handler<AsyncResult<JsonObject>> resultHandler) {
		JsonObject result = new JsonObject();
		DbRequest dbRequest = new DbRequest(dataType, new JsonObject());
		vertx.eventBus().request(dbFind, dbRequest, reply -> {
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

	/**
	 * Apply data operation to fetched data.
	 *
	 * @param dataOperation			Determines which manipulation should be applied.
	 * @param resultHandler			Handles the request to the data operation verticle. Can be failed or succeeded.
	*/
	private void applyManipulation(DataOperation dataOperation, Handler<AsyncResult<JsonObject>> resultHandler) {
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
	}

	private static record Configuration(
			@JsonProperty Map<String, Class<?>> dataTypeMap,
			@JsonProperty Map<String, DataOperation> dataOperationMap
	) {
		private Configuration() {
			this(Collections.emptyMap(), Collections.emptyMap());
		}
	}
}
