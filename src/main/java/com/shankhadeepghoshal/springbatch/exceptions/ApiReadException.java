package com.shankhadeepghoshal.springbatch.exceptions;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
public class ApiReadException extends RuntimeException { // NOPMD

    public ApiReadException(Throwable exception) {
        super(exception);
    }
}
