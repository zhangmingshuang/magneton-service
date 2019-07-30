package com.magneton.service.core.spring.converter;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import org.springframework.http.MediaType;

/**
 * @author zhangmsh
 * @since 2018-12-11 10:52
 * 用来配置默认使用的fastjson序列化配置
 */
public interface FastjsonHttpMessageConvertersConfig {

    /**
     * 配置fastjson的支持的medisType
     *
     * @return 支持的MediaType
     * @see MediaType#valueOf(String)
     */
    MediaType[] mediaTypes();

    /**
     * fastjson的配置
     *
     * @return 配置信息
     */
    FastJsonConfig fastJsonConfig();
}
