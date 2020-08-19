package org.telestion.core.message;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JsonMessageCodecTest {

    @Test void testEncodeDecode(){
        var expected = new Position(5.3, 4.2, 7.1);
        var buf = Buffer.buffer();
        JsonMessageCodec.Instance.encodeToWire(buf, expected);
        var actual = JsonMessageCodec.Instance.decodeFromWire(0, buf);
        Assertions.assertEquals(expected, actual);
    }
}
