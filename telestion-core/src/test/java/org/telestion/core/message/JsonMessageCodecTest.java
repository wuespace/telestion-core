package org.telestion.core.message;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class JsonMessageCodecTest {

    @Test void testEncodeDecode(){
        var expected = new Position(5.3, 4.2, 7.1);
        var buf = Buffer.buffer();
        var codec = JsonMessageCodec.Instance(Position.class);
        codec.encodeToWire(buf, expected);
        var actual = codec.decodeFromWire(0, buf);
        Assertions.assertEquals(expected, actual);
    }

    @Test void testMultiRegister(Vertx vertx, VertxTestContext testContext) throws Throwable {
        vertx.eventBus().registerDefaultCodec(Position.class, JsonMessageCodec.Instance(Position.class));
        vertx.eventBus().registerDefaultCodec(Positions.class, JsonMessageCodec.Instance(Positions.class));
        testContext.completeNow();
    }
}
