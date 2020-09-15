package com.qinchen.chat.common.exception;

/**
 * Created by bxl on 2018/9/17.
 */
public class CoreException extends RuntimeException {

    public CoreException(String message) {
        super(message);
    }

    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException(Throwable cause) {
        super(cause);
    }

}
