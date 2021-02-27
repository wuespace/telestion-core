package org.telestion.core.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.api.config.Config;
import org.telestion.core.message.Address;
import org.telestion.core.message.DbRequest;
import org.telestion.core.message.DbResponse;

/**
 * MongoDatabaseService is a verticle which connects to a local running MongoDB-Database and listens for incoming
 * database requests to process.
 * TODO: Each database implementation (currently only MongoDB) is written in its own DBClient,
 * TODO: but listens to the same address for DBRequests. The address is the interface to the database implementation,
 * TODO: so that the used DB can be replaced easily by spawning another DBClient.
 * Mongo specific:
 * Data is always saved in their exclusive collection which is always named after their Class.name / MessageType.
 */
public final class MongoDatabaseService extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(MongoDatabaseService.class);
	private final Configuration forcedConfig;
	private Configuration config;
	private MongoClient client;

	/**
	 * MongoDB Eventbus Addresses.
	 */
	private final String inSave = Address.incoming(MongoDatabaseService.class, "save");
	private final String outSave = Address.outgoing(MongoDatabaseService.class, "save");
	private final String inFind = Address.incoming(MongoDatabaseService.class, "find");

	/**
	 * This constructor supplies default options.
	 *
	 * @param dbName			the name of the local running database
	 * @param dbPoolName		the name of the database pool
	 */
	public MongoDatabaseService(String dbName, String dbPoolName) {
		this.forcedConfig = new Configuration(
				new JsonObject().put("db_name", dbName).put("useObjectId", true), dbPoolName);
	}

	/**
	 * If this constructor is used at all, settings have to be specified in the config file.
	 */
	public MongoDatabaseService() {
		this.forcedConfig = null;
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(forcedConfig, config(), Configuration.class);
		this.client = MongoClient.createShared(vertx, config.dbConfig, config.dbPoolName);
		this.registerConsumers();
		startPromise.complete();
	}

	/**
	 * Method to register consumers to the eventbus.
	 */
	private void registerConsumers() {
		vertx.eventBus().consumer(inSave, document -> {
			JsonMessage.on(JsonMessage.class, document, this::save);
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

	/**
	 * Save the received document to the database.
	 * If a MongoDB-ObjectId is specified data will be upserted, meaning if the id does not exist it will be inserted,
	 * otherwise it will be updated. Else it will be inserted with a new id.
	 *
	 * @param document a JsonMessage validated through the JsonMessage.on method
	 */
	private void save(JsonMessage document) {
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

	/**
	 * Find the latest entry of the requested data type.
	 *
	 * @param request	DbRequest = { class of requested data type, query? }
	 * @param handler	Result handler, can be failed or succeeded
	 */
	private void findLatest(DbRequest request, Handler<AsyncResult<List<JsonObject>>> handler) {
		FindOptions findOptions = new FindOptions()
				.setSort(new JsonObject().put("_id", -1)).setLimit(1); // last item
		client.findWithOptions(request.dataType().getName(),
				request.query(),
				findOptions, res -> {
					if (res.failed()) {
						logger.error("DB Request failed: ", res.cause());
						handler.handle(Future.failedFuture(res.cause()));
						return;
					}
					handler.handle(Future.succeededFuture(res.result()));
				});
	}

	private static record Configuration(@JsonProperty JsonObject dbConfig, @JsonProperty String dbPoolName) {
		private Configuration() {
			this(new JsonObject().put("db_name", "raketenpraktikum").put("useObjectId", true), "raketenpraktikumPool");
		}
	}
}
