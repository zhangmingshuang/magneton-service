package com.magneton.service.core.signature;

/**
 * @author zhangmingshuang
 * @since 2019/6/11
 */
public abstract class AbstractSignatureBuilder<T extends AbstractSignatureBuilder> {

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
     * 请求时间判定超过则认为超时，秒
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

    /**
     * 设置过滤字段名称
     * 设置的过滤字段名称将不参与签名串的生成
     *
     * @param exclude 过滤字段
     * @return AbstractSignatureBuilder
     */
    public T exclude(String... exclude) {
        this.exclude = exclude;
        return (T) this;
    }

    /**
     * 设置私有密钥
     * 请求串正常情况下以协定的特定规则进行签名串生成
     * 该字段的设置是来用加大规则之外的特定信息
     *
     * @param securityKey 密钥串
     * @return AbstractSignatureBuilder
     */
    public T securityKey(String securityKey) {
        this.securityKey = securityKey;
        return (T) this;
    }

    /**
     * 配置是否启用日志打印
     * 如果设置为true，在请求存在失败情况时，会打印日志信息
     *
     * @param log true/false
     * @return AbstractSignatureBuilder
     */
    public T log(boolean log) {
        this.log = log;
        return (T) this;
    }

    /**
     * 设置请求中的时间字段名称，默认{@code time}
     * 时间字段名称是用来获取请求中的时间数据，用来执行超时判断的
     * 如果设置为{@code null}或者{@code ""}那么表示请求不需要进行时间的判断
     * 同时，如果不需要判断，那么字段  {@link #timeOut}失效
     * 时间的超时时间由{@link #timeOut}来设置
     *
     * @param time 时间字段名称
     * @return AbstractSignatureBuilder
     */
    public T time(String time) {
        this.time = time;
        return (T) this;
    }

    /**
     * 设置数据摘要字段名称，默认{@code sign}
     * 数据摘要字段名称是用来获取请求中的数据摘要的数据
     * 正常情况下数据摘要字段是不参与数据摘要的生成串组装的，所以该名称也可以用来过滤签名串
     * 具体的字段使用规则由{@link #build()}实现类来具体实现
     *
     * @param sign 签名字段名称
     * @return AbstractSignatureBuilder
     */
    public T sign(String sign) {
        this.sign = sign;
        return (T) this;
    }

    /**
     * 设置必须字段
     * 设置的必须字段在请求时都会判断请求中的参数是否包括所有的必须字段
     * 如果有字段未被包括，认为请求数据异常，拒绝请求
     *
     * @param required 必须字段
     * @return AbstractSignatureBuilder
     */
    public T required(String... required) {
        this.required = required;
        return (T) this;
    }

    /**
     * 设置时间超时的范围，如果超过该时间则认为请求超时，拒绝请求
     * 可以用来直接过滤请求
     *
     * @param timeOut 超时时间，毫秒
     * @return AbstractSignatureBuilder
     */
    public T timeOut(long timeOut) {
        this.timeOut = timeOut;
        return (T) this;
    }

    /**
     * 创建具体的签名类
     *
     * @return Signature
     */
    public abstract Signature build();


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
