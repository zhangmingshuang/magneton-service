package com.magneton.service.core.spring.exception;

import com.magneton.service.core.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public interface GlobalExceptionHandler {

    boolean handleException(Throwable e);

    Result handle(Exception e, HttpServletRequest request, HttpServletResponse response);
}
