package org.telestion.core.verticle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.telestion.core.monitoring.MessageLogger;

@ExtendWith(VertxExtension.class)
public class MessageLoggerTest {

    @Test
    void test(Vertx vertx, VertxTestContext testContext) throws Throwable {
        // create watcher for logs
        var logger = LoggerFactory.getLogger(MessageLogger.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        ((Logger) logger).addAppender(listAppender);

        vertx.eventBus().consumer("addr1", message -> {
        });

        // start test case
        vertx.deployVerticle(MessageLogger.class.getName(), event -> {
            Assertions.assertTrue(event.succeeded());

            vertx.eventBus().publish("addr1", "testString");

            vertx.setTimer(Duration.ofSeconds(1).toMillis(), timerId -> {
                // remove apprender
                ((Logger) logger).detachAppender(listAppender);
                listAppender.stop();

                // check state
                assertThat(listAppender.list.size(), is(2));

                var entry = listAppender.list.get(0);
                assertThat(entry.getLevel(), equalTo(Level.INFO));
                assertThat(entry.getMessage(), equalTo("Outbound message to {}: {}"));
                assertThat(entry.getArgumentArray()[0], equalTo("addr1"));
                assertThat(entry.getArgumentArray()[1], equalTo("testString"));
                assertThat(entry.getMDCPropertyMap().size(), equalTo(5));

                entry = listAppender.list.get(1);
                assertThat(entry.getLevel(), equalTo(Level.INFO));
                assertThat(entry.getMessage(), equalTo("Inbound message to {}: {}"));
                assertThat(entry.getArgumentArray()[0], equalTo("addr1"));
                assertThat(entry.getArgumentArray()[1], equalTo("testString"));
                assertThat(entry.getMDCPropertyMap().size(), equalTo(5));

                // we are done now
                testContext.completeNow();
            });
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

}
