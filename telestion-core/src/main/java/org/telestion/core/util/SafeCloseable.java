package org.telestion.core.util;

import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * A {@link Closeable} which does not throw {@link IOException IOExceptions}.
 * This enables the try-close-pattern without catching of exceptions.
 * 
 * @version 1.0
 * @author Jan von Pichovsky, Cedric Boes
 * @see Closeable
 */
public interface SafeCloseable extends Closeable {

    /**
     * Same as {@link Closeable#close()} but it is not allowed to throw an {@link IOException}
     */
    void close();

    /**
     * Wraps the given {@link Closeable} into a {@link SafeCloseable} which logs and prints the <code>stacktrace</code>
     * of the thrown {@link IOException}.
     *
     * @param closeable {@link Closeable} to be wrapped
     * @return new {@link SafeCloseable} from the given {@link Closeable}
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
