package org.telestion.core.logging;

import org.slf4j.MDC;
import org.telestion.core.util.SafeCloseable;

import io.vertx.core.eventbus.Message;

/**
 * A MDC implementation for the {@link Message} object.
 * 
 * @version 1.0
 * @author Jan von Pichowski, Cedric Boes
 * @see MDC
 * @see Message
 */
public final class MessageMDC {

    /**
     * Puts a message object to the MDC store and returns a {@link SafeCloseable} almost like
     * {@link MDC#putCloseable(String, String)}.</br>
     * </br>
     * <i>See {@link MessageMDC#put(String, Message)} for the elements which are put to the store.</i>
     *
     * @param key to identify the {@link Message} in the store
     * @param message {@link Message} to store
     * @return A {@link SafeCloseable} which can remove the key if {@link SafeCloseable#close() close()} is called
     */
    public static SafeCloseable putCloseable(String key, Message<?> message){
        put(key, message);
        return () -> remove(key);
    }

    /**
     * Puts a message object to the MDC store like {@link MDC#put(String, String)}.</br>
     * </br>
     * Following objects are stored:</br>
     * <ul><li>address</li>
     * <li>replyAddress</li>
     * <li>headers</li>
     * <li>send</li>
     * <li>body</li></ul>
     * </br>
     * The key is either the name of the objects, if the key is <code>null</code> or <code>key.name</code>.
     *
     * @param key to identify the {@link Message} in the store
     * @param message {@link Message} to store
     */
    public static void put(String key, Message<?> message){
        String prefix = (key == null ? "" : key+".");
        MDC.put(prefix+"address", message.address());
        MDC.put(prefix+"replyAddress", message.replyAddress());
        MDC.put(prefix+"headers", message.toString());
        MDC.put(prefix+"send", Boolean.toString(message.isSend()));
        MDC.put(prefix+"body", message.body() == null ? null : message.body().toString());
    }

    /**
     * Same as {@link MDC#clear()}.
     */
    public static void clear(){
        MDC.clear();
    }

    /**
     * Removes the message object with the given key from the MDC store (like {@link MDC#remove(String)}).
     *
     * @param key identifies the {@link Message} to remove
     */
    public static void remove(String key){
        String prefix = (key == null ? "" : key + ".");
        MDC.remove(prefix + "address");
        MDC.remove(prefix + "replyAddress");
        MDC.remove(prefix + "headers");
        MDC.remove(prefix + "send");
        MDC.remove(prefix + "body");
    }

}
