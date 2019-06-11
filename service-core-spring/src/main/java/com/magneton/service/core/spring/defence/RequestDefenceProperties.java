package com.magneton.service.core.spring.defence;

import com.magneton.service.core.defence.RequestDefenceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author zhangmingshuang
 * @since 2019/6/4
 */
@ConfigurationProperties(prefix = RequestDefenceProperties.PREFIX, ignoreInvalidFields = true)
public class RequestDefenceProperties extends RequestDefenceConfig {

    public static final String PREFIX = "magneton.request.defence";
    public static final String MODE_LOCAL = "local";
    public static final String MODE_DISTRIBUTED = "distributed";
    /**
     * 防御节点类型
     * local 默认的。表示使用{@link com.magneton.service.core.defence.GuavaCacheRequestDefence}
     * distributed 表示使用 {@link RedisRequestDefence} 此时需要项目引入RedisTemplate包，否则还是会使用local模式
     */
    private String mode = MODE_LOCAL;

    public boolean isLocal() {
        return MODE_LOCAL.equalsIgnoreCase(mode);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
