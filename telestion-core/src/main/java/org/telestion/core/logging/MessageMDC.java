package org.telestion.core.logging;

import io.vertx.core.eventbus.Message;
import org.slf4j.MDC;
import org.telestion.core.util.SafeCloseable;

public final class MessageMDC {

    public static SafeCloseable putCloseable(String key, Message<?> message){
        put(key, message);
        return () -> remove(key);
    }

    public static void put(String key, Message<?> message){
        String prefix = (key == null ? "" : key+".");
        MDC.put(prefix+"address", message.address());
        MDC.put(prefix+"replyAddress", message.replyAddress());
        MDC.put(prefix+"headers", message.toString());
        MDC.put(prefix+"send", Boolean.toString(message.isSend()));
        MDC.put(prefix+"body", message.body() == null ? null : message.body().toString());
    }

    public static void clear(){
        MDC.clear();
    }

    public static void remove(String key){
        String prefix = (key == null ? "" : key + ".");
        MDC.remove(prefix + "address");
        MDC.remove(prefix + "replyAddress");
        MDC.remove(prefix + "headers");
        MDC.remove(prefix + "send");
        MDC.remove(prefix + "body");
    }

}
