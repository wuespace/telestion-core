package org.telestion.core.verticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.JsonMessageCodec;
import org.telestion.core.message.Position;
import org.telestion.core.message.Positions;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(VertxExtension.class)
public class HistoryCacheTest {

    @Test
    void testPublish1(Vertx vertx, VertxTestContext testContext) throws Throwable {

        var address = "addr1";
        var expected = new Position(0.2, 0.1, 7.3);
        var messageName = Position.class.getSimpleName();
        var historySize = 20;
        var apiAddr = HistoryCache.class.getSimpleName();
        var request = new HistoryCache.Request(Position.class, 5);

        vertx.eventBus().registerDefaultCodec(JsonMessage.class, JsonMessageCodec.instance(JsonMessage.class));
        //vertx.eventBus().registerDefaultCodec(HistoryCache.Response.class, JsonMessageCodec.Instance(HistoryCache.Response.class));
        DeploymentOptions options = new DeploymentOptions()
            .setConfig(new JsonObject()
                    .put("messageName", messageName)
                    .put("addressName", address)
                    .put("historySize", historySize)
            );

        //start test case
        vertx.deployVerticle(HistoryCache.class.getName(), options, event -> {
            Assertions.assertTrue(event.succeeded());
            vertx.eventBus().publish(address, expected);
            vertx.eventBus().request(apiAddr, request, responseResult -> {
                Assertions.assertTrue(responseResult.succeeded());
                if(responseResult.result().body() instanceof HistoryCache.Response response){
                    var positions = response.as(Position.class);
                    assertThat(positions, contains(expected));
                }
                testContext.completeNow();
            });
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

}
