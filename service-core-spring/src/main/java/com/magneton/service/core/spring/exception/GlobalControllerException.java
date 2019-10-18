package com.magneton.service.core.spring.exception;

import com.magneton.service.core.Result;
import com.magneton.service.core.ResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/4/11
 */
@ControllerAdvice
public class GlobalControllerException {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerException.class);

    @Autowired(required = false)
    private GlobalExceptionHandler[] handlers;

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHand(Exception e, HttpServletRequest request, HttpServletResponse response) {
        if (handlers != null) {
            for (GlobalExceptionHandler handler : handlers) {
                if (handler.handleException(e)) {
                    Result result = handler.handle(e, request, response);
                    if (response != null) {
                        return result;
                    }
                }
            }
        }
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return Result.fail("not support");
        }
        if (e instanceof ResultException) {
            ResultException fail = (ResultException) e;
            return Result.fail(fail.result());
        }
        LOGGER.error("ControllerExceptionInfo>url:{}", request.getRequestURI());
        LOGGER.error("ControllerException", e);
        return Result.fail();
    }
}
