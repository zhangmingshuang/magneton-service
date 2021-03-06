package com.magneton.service.core.signature;


import com.google.common.hash.Hashing;
import com.magneton.service.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * 提供数据摘要签名
 *
 * @author zhangmingshuang
 * @since 2019/3/6
 */
public class DefaultHttpServletRequestSignature implements Signature {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpServletRequestSignature.class);

    private String time;
    private String sign;
    private String[] required;
    private boolean log;
    private String securityKey;
    private String[] exclude;
    private TimeoutValidator timeoutValidator;

    private DefaultHttpServletRequestSignature(Builder builder) {
        this.sign = builder.getSign();
        this.required = builder.getRequired();
        if (this.required == null) {
            this.required = new String[0];
        }
        this.log = builder.isLog();
        this.securityKey = builder.getSecurityKey();
        this.exclude = builder.getExclude();
        this.time = builder.getTime();
        if (!StringUtil.isEmpty(this.time)) {
            this.timeoutValidator = new TimeoutValidator(builder.getTimeOut(),
                    builder.getTimeOutExclude(),
                    builder.getTimeOutExcludePath());
        }
        builder = null;
    }


    /**
     * 验证请求签名
     * <p>
     * 从请求 {@ServletRequest}中读取所有的请求参数，如果不存在任何参数，则此次验签为失败
     * <p>
     * 必须存在的设定包括：{@code time}, {@code sign}, {@code required}的设定，
     * 祥见 {@link DefaultHttpServletRequestSignature.Builder}
     * <p>
     * 验证字段排序规则为：{@link #signContent(Map)}
     *
     * @param servletRequest 请求
     * @return {@code  true} 验证通过 {@code false} 验证失败
     */
    @Override
    public boolean validate(ServletRequest servletRequest) {
        Map<String, String[]> parameterMap = servletRequest.getParameterMap();
        if (parameterMap == null) {
            if (log) {
                LOGGER.info("请求异常，不存在请求参数。");
            }
            return false;
        }
        if (this.timeoutValidator != null) {
            Long t = this.getLong(parameterMap, time);
            if (t == null) {
                if (log) {
                    LOGGER.info("请求异常，请求参数{}不存在", time);
                }
                return false;
            }
            boolean isTimeOut = this.timeoutValidator.isTimeOut(t.longValue());
            if (isTimeOut) {
                //如果请求中的时间参数与系统时间超过了timeOut设定
                //判断是否为允许过滤
                String url = ((HttpServletRequest) servletRequest).getRequestURI();
                if (!this.timeoutValidator.isAllowExclude(t.longValue(), url)) {
                    if (log) {
                        LOGGER.info("请求异常，请求时间参数{}与当前时间已经超过{}s了", t, time);
                    }
                    return false;
                }
            }
        }

        String s = this.getString(parameterMap, sign);
        if (s == null || s.trim().length() < 1) {
            if (log) {
                LOGGER.info("请求异常，签名参数{}不存在", sign);
            }
            return false;
        }
        //验证是否存在必须参数
        for (int i = required.length - 1; i > 0; --i) {
            String key = required[i];
            if (!parameterMap.containsKey(key)) {
                if (log) {
                    LOGGER.info("必须参数{}不存在", key);
                }
                return false;
            }
        }
        String requestSign = this.sign(parameterMap);
        if (!s.equals(requestSign)) {
            if (log) {
                String content = this.signContent(parameterMap);
                LOGGER.info("请求异常，签名错误，应{}，但{}不存在，签名串：{}", requestSign, s, content);
            }
            return false;
        }
        return true;
    }

    /**
     * 签名
     *
     * @param parameterMap 请求Key-Value
     * @return 签名
     */
    @Override
    public String sign(Map<String, String[]> parameterMap) {
        String content = this.signContent(parameterMap);
        if (content == null) {
            return null;
        }
        return Hashing.sha256().hashBytes(content.getBytes()).toString();
    }

    /**
     * 组装签名串
     *
     * <p>
     * 组装规则说明：
     * 根据Key，使用 {@code Collections.sort(keys)}之后，获取value
     * 然后组装成 {@code key=value&key2=value2&securityKey} 的格式
     * 如果securityKey为空或空字符串，则最后不拼接
     * 即{@code key=value&key2=value2}
     *
     * @param parameterMap 请求Key-Value
     * @return 组装之后的签名串
     */
    @Override
    public String signContent(Map<String, String[]> parameterMap) {
        ArrayList<String> keys = new ArrayList(parameterMap.keySet());
        Collections.sort(keys);

        StringBuilder content = new StringBuilder();
        for (int i = 0, s = keys.size(); i < s; ++i) {
            String k = keys.get(i);
            if (this.isSkip(k)) {
                continue;
            }
            String v = getString(parameterMap, k);
            if (v == null || v.length() < 1) {
                continue;
            }
            content.append(k).append('=').append(v).append('&');
        }

        if (securityKey != null && securityKey.length() > 0) {
            content.append(securityKey);
            return content.toString();
        }
        return content.substring(0, content.length() - 1);
    }


    public boolean isSkip(String key) {
        if (key == null || key.length() < 1) {
            return true;
        }
        if (sign.equals(key)) {
            return true;
        }
        if (exclude != null) {
            for (String e : exclude) {
                if (e.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getString(Map<String, String[]> parameterMap, String key) {
        String[] values = parameterMap.get(key);
        if (values == null || values.length < 1) {
            return null;
        }
        return values[0];
    }

    private Long getLong(Map<String, String[]> parameterMap, String key) {
        String val = getString(parameterMap, key);
        if (val == null) {
            return null;
        }
        return Long.parseUnsignedLong(val);
    }

    public static class Builder extends AbstractSignatureBuilder<Builder> {

        private long timeOutExclude = -1;
        private String[] timeOutExcludePath;

        /**
         * 设置时间超时的判断过滤地址的超时时间
         * 如果设置为小于等于0表示设置的过滤地址不需要计算是否超时
         *
         * @param timeOutExclude 过滤地址的超时时间,毫秒
         * @return Builder
         */
        public Builder timeOutExclude(long timeOutExclude) {
            this.timeOutExclude = timeOutExclude;
            return this;
        }

        /**
         * 设置时间超时的判断过滤地址
         *
         * @param excludePath 时间超时的判断过滤地址
         * @return Builder
         */
        public Builder timeOutExcludePath(String... timeOutExcludePath) {
            this.timeOutExcludePath = timeOutExcludePath;
            return this;
        }

        @Override
        public DefaultHttpServletRequestSignature build() {
            return new DefaultHttpServletRequestSignature(this);
        }


        public String[] getTimeOutExcludePath() {
            return timeOutExcludePath;
        }

        public long getTimeOutExclude() {
            return timeOutExclude;
        }
    }

    @Override
    public String getSign() {
        return sign;
    }
}
