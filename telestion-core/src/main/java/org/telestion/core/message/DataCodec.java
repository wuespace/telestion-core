package org.telestion;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.spi.json.JsonCodec;

public final class DataCodec implements MessageCodec<Data, Data> {

    public static final DataCodec Instance = new DataCodec();

    private DataCodec() { }

    @Override
    public void encodeToWire(Buffer buffer, Data data) {
        var buf = JsonCodec.INSTANCE.toBuffer(data);
        buffer.appendInt(buf.length()).appendBuffer(buf);
    }

    @Override
    public Data decodeFromWire(int pos, Buffer buffer) {
        var len = buffer.getInt(pos);
        return JsonCodec.INSTANCE.fromBuffer(buffer.getBuffer(pos+4, pos+4+len), Data.class);
    }

    @Override
    public Data transform(Data data) {
        return data;
    }

    @Override
    public String name() {
        return DataCodec.class.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
