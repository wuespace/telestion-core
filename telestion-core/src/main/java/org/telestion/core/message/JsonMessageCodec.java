package org.telestion.core.message;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.spi.json.JsonCodec;

public final class JsonMessageCodec implements MessageCodec<JsonMessage, JsonMessage> {

    public static final JsonMessageCodec Instance = new JsonMessageCodec();

    private JsonMessageCodec(){}

    @Override
    public void encodeToWire(Buffer buffer, JsonMessage jsonMessage) {
        var nameBuf = Buffer.buffer(jsonMessage.getClass().getName());
        buffer.appendInt(nameBuf.length());
        buffer.appendBuffer(nameBuf);
        var messageBuf = JsonCodec.INSTANCE.toBuffer(jsonMessage);
        buffer.appendInt(messageBuf.length());
        buffer.appendBuffer(messageBuf);
    }

    @Override
    public JsonMessage decodeFromWire(int pos, Buffer buffer) {
        var nameLen = buffer.getInt(pos);
        var name = buffer.getBuffer(pos+4, pos+4+nameLen).toString();
        var messageLen = buffer.getInt(pos+4+nameLen);
        var message = buffer.getBuffer(pos+4+nameLen+4, pos+4+nameLen+4+messageLen);

        try {
            var result = JsonCodec.INSTANCE.fromBuffer(message, Class.forName(name));
            return (JsonMessage) result;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonMessage transform(JsonMessage jsonMessage) {
        return jsonMessage;
    }

    @Override
    public String name() {
        return JsonMessageCodec.class.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
