package de.wuespace.telestion.services.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.message.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PeriodicDataPublisher extends AbstractVerticle {
	private final Configuration forcedConfig;
	private Configuration config;

	private final Logger logger = LoggerFactory.getLogger(PeriodicDataPublisher.class);
	private Handler<Long> reqTimer;
	private Long timer;
	private DbRequest dbRequest;
	private String timeOfLastDataSet = null;

	private String out = Address.outgoing(PeriodicDataPublisher.class);
	private final String db = Address.incoming(MongoDatabaseService.class, "find");

	public PeriodicDataPublisher() { this.forcedConfig = null; }

	public PeriodicDataPublisher(String collection, int rate, String outAddress) {
		this.forcedConfig = new Configuration(
				collection, "", Collections.emptyList(), Collections.emptyList(), rate, outAddress
		);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		config = Config.get(forcedConfig, config(), Configuration.class);
		dbRequest = getDbRequestFromConfig();
		timer = getRateInMillis(config.rate());
		reqTimer = id -> {
			databaseRequest();
			vertx.setTimer(timer, reqTimer);
		};
		vertx.setTimer(timer, reqTimer);
		startPromise.complete();
	}

	private void databaseRequest() {
		if (timeOfLastDataSet != null) {
			var dateQuery = new JsonObject()
				.put("datetime", new JsonObject().put("$gt", new JsonObject().put("$date", timeOfLastDataSet)));
			dbRequest = getDbRequestFromConfig(dateQuery.toString());
		}
		logger.info(dbRequest.query().toString());
		vertx.eventBus().request(db, dbRequest.json(), (Handler<AsyncResult<Message<JsonObject>>>) reply -> {
			if (reply.failed()) {
				logger.error(reply.cause().getMessage());
				return;
			}

			var jArr = reply.result().body().getJsonArray("result");
			// Set timeOfLastDataSet to the datetime of the last received data
			timeOfLastDataSet = jArr.getJsonObject(jArr.size()-1).getJsonObject("datetime").getString("$date");
			vertx.eventBus().publish(config.outAddress(), jArr);
		});
	}

	private DbRequest getDbRequestFromConfig() {
		// TODO: Make parameters optional and config easier.
		return new DbRequest(
				config.collection(),
				config.query(),
				config.fields(),
				config.sort(),
				-1,
				0
		);
	}

	private DbRequest getDbRequestFromConfig(String query) {
		return new DbRequest(
				config.collection(),
				query,
				config.fields(),
				config.sort(),
				-1,
				0
		);
	}

	private static long getRateInMillis(int rate) {
		BigDecimal bd = new BigDecimal((double) (1 / rate));
		bd = bd.setScale(3, RoundingMode.HALF_UP);
		return (long) bd.doubleValue() * 1000L;
	}

	private static record Configuration(
			@JsonProperty String collection,
			@JsonProperty String query,
			@JsonProperty List<String> fields,
			@JsonProperty List<String> sort,
			@JsonProperty int rate,
			@JsonProperty String outAddress
	) {
		private Configuration() {
			this("", "", Collections.emptyList(), Collections.emptyList(), 0, "");
		}
	}
}
