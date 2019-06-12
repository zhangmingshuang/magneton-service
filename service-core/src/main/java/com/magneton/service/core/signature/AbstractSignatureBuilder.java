package com.magneton.service.core.signature;

/**
 * @author zhangmingshuang
 * @since 2019/6/11
 */
public abstract class AbstractSignatureBuilder<T> {

    /**
     * Http请求的时间字段名称
     */
    private String time = "time";
    /**
     * 签名名称
     */
    private String sign = "sign";
    /**
     * 必须字段名称
     */
    private String[] required;
    /**
     * 排除字段名称，过滤的请求参数
     */
    private String[] exclude;
    /**
     * 请求时间判定超过则认为超时
     */
    private long timeOut = 300;

    /**
     * 是否启用日志输出
     */
    private boolean log = false;
    /**
     * 私有密钥
     */
    private String securityKey;

    public AbstractSignatureBuilder exclude(String... exclude) {
        this.exclude = exclude;
        return this;
    }

    public AbstractSignatureBuilder securityKey(String securityKey) {
        this.securityKey = securityKey;
        return this;
    }

    public AbstractSignatureBuilder log(boolean log) {
        this.log = log;
        return this;
    }

    public AbstractSignatureBuilder time(String time) {
        this.time = time;
        return this;
    }

    public AbstractSignatureBuilder sign(String sign) {
        this.sign = sign;
        return this;
    }

    public AbstractSignatureBuilder required(String... required) {
        this.required = required;
        return this;
    }

    public AbstractSignatureBuilder timeOut(long timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public abstract T build();


    public String getTime() {
        return time;
    }

    public String getSign() {
        return sign;
    }

    public String[] getRequired() {
        return required;
    }

    public String[] getExclude() {
        return exclude;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public boolean isLog() {
        return log;
    }

    public String getSecurityKey() {
        return securityKey;
    }
}
