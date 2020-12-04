package org.telestion.core.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.telestion.core.message.DataOperation;
import org.telestion.core.message.DataRequest;

public interface IDataRequestService {
	void dataRequestDispatcher(DataRequest request, Handler<AsyncResult<JsonObject>> resultHandler);

	@Fluent
	IDataRequestService fetchLatestData(List<Class<?>> dataTypes, Handler<AsyncResult<JsonArray>> resultHandler);

	@Fluent
	IDataRequestService fetchLatestData(Class<?> dataType, Handler<AsyncResult<JsonObject>> resultHandler);

	@Fluent
	IDataRequestService applyManipulation(DataOperation dataOperation, Handler<AsyncResult<JsonObject>> handler);
}
