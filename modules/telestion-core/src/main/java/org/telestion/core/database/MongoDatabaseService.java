package org.telestion.core.database;

import de.jvpichowski.rocketsound.messages.GPSPosition;
import de.jvpichowski.rocketsound.messages.Position;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.DbRequest;
import org.telestion.core.message.DbResponse;
import org.telestion.core.monitoring.MessageLogger;

import java.util.List;

public final class MongoDatabaseService extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(MongoDatabaseService.class);
	private JsonObject dbConfig;
	private String dbPoolName;
	private MongoClient client;

	private final String inSave = Address.incoming(MongoDatabaseService.class, "save");
	private final String outSave = Address.outgoing(MongoDatabaseService.class, "save");
	private final String inFind = Address.incoming(MongoDatabaseService.class, "find");
	private final String outFind = Address.outgoing(MongoDatabaseService.class, "find");

	public MongoDatabaseService(String dbName, String dbPoolName) {
		this.dbConfig = new JsonObject().put("db_name", dbName).put("useObjectId", true);
		this.dbPoolName = dbPoolName;
	}

	public MongoDatabaseService() {}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MongoDatabaseService("raketenpraktikum", "raketenpraktikumPool"));
		// deploy random position publisher
		vertx.deployVerticle(new MessageLogger());
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		this.client = MongoClient.createShared(vertx, this.dbConfig, this.dbPoolName);
		this.registerConsumers();
		startPromise.complete();
	}

	private void registerConsumers() {
		vertx.eventBus().consumer(inSave, document -> {
			JsonMessage.on(Position.class, document, this::save);
			JsonMessage.on(GPSPosition.class, document, this::save);
			// ... register addresses
		});
		vertx.eventBus().consumer(inFind, request -> {
			JsonMessage.on(DbRequest.class, request, dbRequest -> {
				this.findLatest(dbRequest, result -> {
					if (result.failed()) {
						request.fail(-1, result.cause().getMessage());
					}
					if (result.succeeded()) {
						request.reply(result.result());
					}
				});
			});
		});
	}

	private void save(JsonMessage document) {
		logger.debug("Started save");
		var object = document.json();
		client.save(document.className(), object, res -> {
			if (res.failed()) {
				logger.error("DB Save failed: ", res.cause());
				return;
			}
			String id = res.result();
			client.find(document.className(), new JsonObject().put("_id", id), rec -> {
				if (rec.failed()) {
					logger.error("DB Find failed: ", rec.cause());
					return;
				}
				DbResponse dbRes = new DbResponse(document.getClass(), rec.result());
				vertx.eventBus().publish(outSave, dbRes.json());
			});
		});
	}

	private void findLatest(DbRequest request, Handler<AsyncResult<List<JsonObject>>> handler) {
		FindOptions findOptions = new FindOptions()
				.setSort(new JsonObject().put("_id", -1)).setLimit(1); // last item
		client.findWithOptions(request.dataType().getName(),
				request.query().orElse(new JsonObject()),
				findOptions, res -> {
					if (res.failed()) {
						logger.error("DB Request failed: ", res.cause());
						handler.handle(Future.failedFuture(res.cause()));
						return;
					}
					handler.handle(Future.succeededFuture(res.result()));
				});
	}
}

/**
 *
 * The frontend issues requests to a DBRequestAdapter, which splits the requests
 * into its requested components and the desired operation (integrate, multiply, ...)
 * that should be executed onto that data.
 * Each of these operations will be implemented in its own verticle.
 * The DBRequestController (Client of DBRequestFacade) is responsible for collecting all responses returned from the requests
 * and redirects them back to the issuer.
 *
 * Each database implementation (at the moment only MongoDB) is written in its own DBClient,
 * but listens to the same address for DBRequests. The address is the interface to the database implementation,
 * so that the used DB can be replaced easily by spawning another DBClient.
 *
 * Mongo specific:
 * Data is always saved in their exclusive collection which is always named after their Class.name / MessageType.
 *
 */

