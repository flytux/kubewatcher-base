package com.kubeworks.watcher.base;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -2239980524597437084L;

    public BaseException(final String message) {
        super(message);
    }

    public BaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
