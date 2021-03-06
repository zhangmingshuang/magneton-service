package com.magneton.service.core;

import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@ToString
public class Result<T> {

    public static final String ERROR = "操作失败";
    public static final String ERROR_CODE = "1";
    public static final String SUCCESS = "操作成功";
    public static final String SUCCESS_CODE = "0";

    private String code;
    private String info;
    private T data;
    private long timestamp;
    private boolean success;
    /**
     * 附加信息
     */
    private Map<String, Object> additional;

    private Result() {

    }

    public Result code(String code) {
        this.code = code;
        return this;
    }

    public Result code(int code) {
        this.code = String.valueOf(code);
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
        result.code = SUCCESS_CODE;
        result.info = SUCCESS;
        return result;

    }

    public final static Result fail(Result res) {
        return res;
    }

    public final static Result fail() {
        Result result = new Result();
        result.code = ERROR_CODE;
        result.info = ERROR;
        return result;
    }

    public final static Result fail(String info) {
        Result result = new Result();
        result.code = ERROR_CODE;
        result.info = info;
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
            result.code = String.valueOf(code);
            return this;
        }

        public Builder code(String code) {
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

    public boolean isSuccessData(String str) {
        if (!isSuccess()) {
            return false;
        }
        return isData(str);
    }

    public boolean isSuccessData(Boolean bool) {
        if (!isSuccess()) {
            return false;
        }
        return isData(bool);
    }

    public boolean isData(String str) {
        return str.equals(data);
    }

    public boolean isData(Boolean bool) {
        return bool.equals(data);
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    public Map<String, Object> getAdditional() {
        return additional == null ? Collections.emptyMap() : additional;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAdditional(Map<String, Object> additional) {
        this.additional = additional;
    }
}
