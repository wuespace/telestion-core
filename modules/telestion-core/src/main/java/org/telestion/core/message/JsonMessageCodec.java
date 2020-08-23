package org.telestion.core.message;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.spi.json.JsonCodec;
import org.telestion.api.message.JsonMessage;

/**
 * The {@link MessageCodec} which encodes all {@link org.telestion.api.message.JsonMessage} through the {@link JsonCodec}.
 * Append this to the Vert.x event bus to enable transmitting of {@link org.telestion.api.message.JsonMessage}.
 */
public final class JsonMessageCodec<T extends JsonMessage> implements MessageCodec<T, T> {

    public static <T extends JsonMessage> MessageCodec<T, T> instance(Class<T> type){
        return new JsonMessageCodec<>(type);
    }

    private final String name;

    private JsonMessageCodec(Class<T> messageClass){
        name = messageClass.getName();
    }

    @Override
    public void encodeToWire(Buffer buffer, T jsonMessage) {
        var nameBuf = Buffer.buffer(jsonMessage.getClass().getName());
        buffer.appendInt(nameBuf.length());
        buffer.appendBuffer(nameBuf);
        var messageBuf = JsonCodec.INSTANCE.toBuffer(jsonMessage);
        buffer.appendInt(messageBuf.length());
        buffer.appendBuffer(messageBuf);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        var nameLen = buffer.getInt(pos);
        var name = buffer.getBuffer(pos+4, pos+4+nameLen).toString();
        var messageLen = buffer.getInt(pos+4+nameLen);
        var message = buffer.getBuffer(pos+4+nameLen+4, pos+4+nameLen+4+messageLen);
        try {
            var result = JsonCodec.INSTANCE.fromBuffer(message, Class.forName(name));
            return (T) result;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T transform(T jsonMessage) {
        return jsonMessage;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
