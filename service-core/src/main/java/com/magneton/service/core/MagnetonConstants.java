package com.magneton.service.core;

import java.nio.charset.Charset;

/**
 * @author zhangmingshuang
 * @since 2019/7/30
 */
public interface MagnetonConstants {
    String PROPERTIES_PREFIX = "magneton";

    Charset DEFAULT_CHARSET = Charset.defaultCharset();

    String DEFAULT_RESOURCE_HANDLER = "/static/**";

    String DEFAULT_RESOURCE_LOCATION = "classpath:/static/";
}
