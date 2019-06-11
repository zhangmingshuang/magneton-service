package com.magneton.service.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class Result<T> {

    private int code;
    private String info;
    private T data;
    private long timestamp;
    private boolean success;
    /**
     * 附加信息
     */
    private Map<String, Object> additional;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result code(int code) {
        this.code = code;
        return this;
    }

    public Result info(String info) {
        this.info = info;
        return this;
    }

    public Result data(T data) {
        this.data = data;
        return this;
    }

    public Result additional(Map<String, Object> additional) {
        this.additional = additional;
        return this;
    }

    public Result additional(String key, Object value) {
        if (this.additional == null) {
            this.additional = new HashMap<>();
        }
        this.additional.put(key, value);
        return this;
    }

    public final static Result success() {
        Result result = new Result();
        result.code = ResultCodeFactory.getSuccessCode();
        result.info = ResultCodeFactory.getSuccessCodeDesc();
        return result;

    }

    public final static Result fail() {
        Result result = new Result();
        result.code = ResultCodeFactory.getErrorCode();
        result.info = ResultCodeFactory.getErrorCodeDesc();
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<T> {
        private Result<T> result;

        public Builder() {
            this.result = new Result();
        }

        public Builder code(int code) {
            result.code = code;
            return this;
        }

        public Builder info(String info) {
            result.info = info;
            return this;
        }

        public Builder data(T data) {
            result.data = data;
            return this;
        }

        public Builder additional(Map<String, Object> additional) {
            result.additional = additional;
            return this;
        }

        public Builder additional(String key, Object value) {
            if (result.additional == null) {
                result.additional = new HashMap<>();
            }
            result.additional.put(key, value);
            return this;
        }

        public Result<T> build() {
            return result;
        }
    }
}
