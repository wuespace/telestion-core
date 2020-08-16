package org.telestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.spi.json.JsonCodec;

public final class Data {

    @JsonProperty private final String source;
    @JsonProperty private final String type;
    @JsonProperty private final String content;

    private Data(){
        this(null, null, null);
    }

    private Data(String source, String type, String content) {
        this.source = source;
        this.type = type;
        this.content = content;
    }

    public Data(AbstractVerticle source, Object data){
        this(source.deploymentID(), data.getClass().getName(), JsonCodec.INSTANCE.toString(data));
    }

    public String source() {
        return source;
    }

    public boolean is(Class<?> type){
        return this.type.equals(type.getName());
    }

    public Object get() {
        try {
            return JsonCodec.INSTANCE.fromString(content, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type){
        if(!is(type)){
            throw new RuntimeException("The requested type ("+type.getName()+") does not match the content type ("+type+")");
        }
        return (T) get();
    }

//    public String getType() {
//        return type;
//    }

//    public String getContent() {
//        return content;
//    }
}
