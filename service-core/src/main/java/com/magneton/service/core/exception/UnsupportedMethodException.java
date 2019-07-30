package com.magneton.service.core.exception;

/**
 * @author zhangmingshuang
 * @since 2019/7/29
 */
public class UnsupportedMethodException extends RuntimeException {

    public UnsupportedMethodException(String message) {
        super(message);
    }

    public UnsupportedMethodException(Throwable e) {
        super(e);
    }

    public UnsupportedMethodException(String message, Throwable e) {
        super(message, e);
    }


}
