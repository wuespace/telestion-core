package org.telestion.core.util;

import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public interface SafeCloseable extends Closeable {

    void close();

    static SafeCloseable from(Closeable closeable){
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
