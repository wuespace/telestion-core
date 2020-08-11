package org.telestion.core.util;

import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * A Closeable which does not throw IOExceptions.
 * This enables the try-close-pattern without catching of exceptions.
 */
public interface SafeCloseable extends Closeable {

    /**
     * Same as in Closeable but it is not allowed to throw an IOException
     */
    void close();

    /**
     * Wrap the give closable by a safe one which logs and prints the stacktrace of the thrown IOException.
     *
     * @param closeable
     * @return
     */
    static SafeCloseable safe(Closeable closeable){
        return () -> {
            try {
                closeable.close();
            }catch (IOException ex){
                LoggerFactory.getLogger(SafeCloseable.class).error(ex.getLocalizedMessage(), ex);
                ex.printStackTrace();
            }
        };
    }
}
