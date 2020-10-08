package org.telestion.core.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.Address;
import org.telestion.core.message.DBRequest;
import org.telestion.core.message.DBResponse;
import org.telestion.core.message.Position;

import java.util.List;

public final class Database extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(Database.class);

    private JsonObject dbConfig;
    private String dbPoolName;
    private MongoClient client;

    private final String inSave = Address.incoming(Database.class, "save");
    private final String outSave = Address.outgoing(Database.class, "save");
    private final String inFind = Address.incoming(Database.class, "find");
    private final String outFind = Address.outgoing(Database.class, "find");

    public Database(String dbName, String dbPoolName) {
        this.dbConfig = new JsonObject().put("db_name", dbName).put("useObjectId", true);
        this.dbPoolName = dbPoolName;
    }

    public Database() {}

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.client = MongoClient.createShared(vertx, this.dbConfig, this.dbPoolName);
        vertx.eventBus().consumer(inSave, document -> {
            JsonMessage.on(Position.class, document, this::save);
        });
        vertx.eventBus().consumer(inFind, request -> {
            JsonMessage.on(DBRequest.class, request, this::find);
        });
        startPromise.complete();
    }

    private void save(JsonMessage document) {
        var object = document.json();
        client.save(document.name(), object, res -> {
            if (res.failed()) {
                logger.error("DB Save failed: ", res.cause());
                return;
            }
            String id = res.result();
            client.find(document.name(), new JsonObject().put("_id", id), rec -> {
                if (rec.failed()) {
                    logger.error("DB Find failed: ", rec.cause());
                    return;
                }
                /**
                 * TODO: Instead of a DBResponse it could be retrieved by name which message type
                 * should be published e.g. Position
                 */
                DBResponse dbRes = new DBResponse(document.name(), rec.result());
                vertx.eventBus().publish(outSave, dbRes.json());
            });
        });
    }

    private void find(DBRequest request) {
        client.find(request.collection(), request.query(), res -> {
           if (res.failed()) {
               logger.error("DB Request failed: ", res.cause());
               return;
           }
           DBResponse dbRes = new DBResponse(request.collection(), res.result());
           vertx.eventBus().publish(outFind, dbRes.json());
        });
    }
}

/**
 *
 * Project = Database name, pool name
 * data_row = {
 *     timestamp: ...,
 *     data_set: <ID of dataset>,
 *     time_rel_to_data_set: ...,
 *     data: {
 *         position: {
 *             x, y, z
 *         },
 *         velocity: {
 *             x, y, z, ...
 *         },
 *         :
 *         :
 *     }
 * }
 *
 *
 */
