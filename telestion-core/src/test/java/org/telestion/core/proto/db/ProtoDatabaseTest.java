package org.telestion.core.proto.db;

import io.vertx.core.Vertx;
import io.vertx.core.spi.json.JsonCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.telestion.api.message.JsonMessage;
import org.telestion.core.message.JsonMessageCodec;
import org.telestion.core.message.Position;
import org.telestion.core.message.Positions;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(VertxExtension.class)
public class ProtoDatabaseTest {

    @Test void testPublish1(Vertx vertx, VertxTestContext testContext) throws Throwable {

        vertx.eventBus().registerDefaultCodec(Position.class, JsonMessageCodec.Instance(Position.class));

        //start test case
        vertx.deployVerticle(ProtoDatabase.class.getName(), event -> {
            Assertions.assertTrue(event.succeeded());
            var expected = new Position(0.2, 0.1, 7.3);
            vertx.eventBus().publish("mavlink", expected);
            vertx.eventBus().request("request#position", null, positionResult -> {
                Assertions.assertTrue(positionResult.succeeded());
                var positionList = JsonMessage.from(positionResult.result().body(), Positions.class);
                assertThat(positionList.list().size(), equalTo(1));
                var actual = positionList.list().get(0);
                assertThat(actual, equalTo(expected));
                testContext.completeNow();
            });
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }


    @Test void testPublish50(Vertx vertx, VertxTestContext testContext) throws Throwable {

        vertx.eventBus().registerDefaultCodec(Position.class, JsonMessageCodec.Instance(Position.class));

        var expected = IntStream.range(0, 50).mapToObj(i -> new Position(0, 1, i)).collect(Collectors.toList());

        //start test case
        vertx.deployVerticle(ProtoDatabase.class.getName(), event -> {
            Assertions.assertTrue(event.succeeded());
            expected.forEach(p ->  vertx.eventBus().publish("mavlink", p));
            Collections.reverse(expected);
        });

        Thread.sleep(Duration.ofSeconds(1).toMillis());

        vertx.eventBus().request("request#position", null, positionResult -> {
            Assertions.assertTrue(positionResult.succeeded());
            var positionList = JsonMessage.from(positionResult.result().body(), Positions.class);
            var actual = positionList.list();
            assertThat(actual, equalTo(expected));
            testContext.completeNow();
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test void testPublish150(Vertx vertx, VertxTestContext testContext) throws Throwable {

        vertx.eventBus().registerDefaultCodec(Position.class, JsonMessageCodec.Instance(Position.class));

        var values = IntStream.range(0, 150).mapToObj(i -> new Position(0, 1, i)).collect(Collectors.toList());

        //start test case
        vertx.deployVerticle(ProtoDatabase.class.getName(), event -> {
            Assertions.assertTrue(event.succeeded());
            values.forEach(p ->  vertx.eventBus().publish("mavlink", p));
        });

        Thread.sleep(Duration.ofSeconds(1).toMillis());
        var expected = values;
        Collections.reverse(expected);
        expected = expected.subList(0, 100);
        var exp = expected;

        vertx.eventBus().request("request#position", null, positionResult -> {
            Assertions.assertTrue(positionResult.succeeded());
            var positionList = JsonMessage.from(positionResult.result().body(), Positions.class);
            var actual = positionList.list();
            assertThat(actual, equalTo(exp));
            testContext.completeNow();
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

}
