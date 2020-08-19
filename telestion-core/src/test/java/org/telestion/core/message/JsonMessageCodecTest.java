package org.telestion.core.message;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.spi.json.JsonCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JsonMessageCodecTest {

    @Test void testEncodeDecode(){
        var expected = new Position(5.3, 4.2, 7.1);
        System.out.println(JsonCodec.INSTANCE.toString(expected));
        var buf = Buffer.buffer();
        System.out.println(buf.toString());
        JsonMessageCodec.Instance.encodeToWire(buf, expected);
        var actual = JsonMessageCodec.Instance.decodeFromWire(0, buf);
        Assertions.assertEquals(expected, actual);
    }
}
