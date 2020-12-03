package org.telestion.core.database;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.DBRequest;
import org.telestion.core.message.DBResponse;
import org.telestion.core.monitoring.MessageLogger;

import java.util.List;

public final class MongoDatabase extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(MongoDatabase.class);

    private JsonObject dbConfig;
    private String dbPoolName;
    private MongoClient client;

    /*
    	Address must be a global known unique address for Database operations,
		because every Database implementation should listen to the same address
		so that the database can be replaced easily.
		TODO: Change incoming and outgoing address!
	*/
    private final String inSave = Address.incoming(MongoDatabase.class, "save");
    private final String outSave = Address.outgoing(MongoDatabase.class, "save");
    private final String inFind = Address.incoming(MongoDatabase.class, "find");
    private final String outFind = Address.outgoing(MongoDatabase.class, "find");

    public MongoDatabase(String dbName, String dbPoolName) {
        this.dbConfig = new JsonObject().put("db_name", dbName).put("useObjectId", true);
        this.dbPoolName = dbPoolName;
    }

    public MongoDatabase() {}

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // we have to register the codec for the used message! Do this in your Launcher.
        vertx.deployVerticle(new MongoDatabase("daedalus2", "daedalus2Pool"));
        vertx.deployVerticle(RandomPositionPublisher.class.getName());
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
		});
		vertx.eventBus().consumer(inFind, request -> {
			JsonMessage.on(DBRequest.class, request, (dbRequest) -> {
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
		vertx.eventBus().consumer(Address.outgoing(RandomPositionPublisher.class, "MockPos"), pos -> {
			logger.debug("Got position: {}", pos.body());
			JsonMessage.on(Position.class, pos, this::save);
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
                DBResponse dbRes = new DBResponse(document.getClass(), rec.result());
                vertx.eventBus().publish(outSave, dbRes.json());
            });
        });
    }

    private void findLatest(DBRequest request, Handler<AsyncResult<List<JsonObject>>> handler) {
    	FindOptions findOptions = new FindOptions()
			.setSort(new JsonObject().put("_id", -1))
			.setLimit(1);
    	client.findWithOptions(request.dataType().getName(),
			request.query().orElse(new JsonObject()), findOptions, res -> {
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
