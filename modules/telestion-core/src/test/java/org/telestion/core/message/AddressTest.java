package org.telestion.core.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class AddressTest {

    @Test
    void test(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var receiver = new Receiver();
        var responder = new Responder();
        var consumer = new Consumer();
        var publisher = new Publisher();
        vertx.deployVerticle(receiver);
        vertx.deployVerticle(responder);
        vertx.deployVerticle(consumer);
        vertx.deployVerticle(publisher);

        Thread.sleep(Duration.ofMillis(50).toMillis());

        assertThat(consumer.publishedMsg, equalTo("Hello"));

        vertx.eventBus().send(Address.incoming(Receiver.class), "Hello");
        Thread.sleep(Duration.ofMillis(10).toMillis());
        assertThat(receiver.sendMsg, equalTo("Hello"));

        vertx.eventBus().request(Address.incoming(Responder.class), "Hello", msgResult -> {
            assertThat(msgResult.result().body(), equalTo("Hello"));
            testContext.completeNow();
        });

        assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    private final class Receiver extends AbstractVerticle {
        public String sendMsg = null;

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            vertx.eventBus().consumer(Address.incoming(this), msg -> {
                this.sendMsg = (String) msg.body();
            });
            startPromise.complete();
        }
    }

    private final class Responder extends AbstractVerticle {
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            vertx.eventBus().consumer(Address.incoming(this), msg -> {
                msg.reply(msg.body());
            });
            startPromise.complete();
        }
    }

    private final class Consumer extends AbstractVerticle {
        public String publishedMsg = null;

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            vertx.eventBus().consumer(Address.outgoing(Publisher.class), msg -> {
                publishedMsg = (String) msg.body();
            });
            startPromise.complete();
        }
    }

    private final class Publisher extends AbstractVerticle {
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            vertx.setTimer(Duration.ofMillis(10).toMillis(), timerId -> {
                vertx.eventBus().publish(Address.outgoing(this), "Hello");
            });
            startPromise.complete();
        }
    }
}
