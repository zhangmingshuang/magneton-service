package com.magneton.service.core.signature;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/3/6
 */
public interface Signature {
    /**
     * 签名验证
     *
     * @param servletRequest 请求
     * @return {@code true}验证通过
     */
    boolean validate(ServletRequest servletRequest);

    /**
     * 请数据进行签名
     *
     * @param parameterMap Key-Value
     * @return 签名串
     */
    String sign(Map<String, String[]> parameterMap);

    /**
     * 对数据进行签名内容拼装
     *
     * @param parameterMap Kye-Value
     * @return String 签名内容
     */
    String signContent(Map<String, String[]> parameterMap);
}
