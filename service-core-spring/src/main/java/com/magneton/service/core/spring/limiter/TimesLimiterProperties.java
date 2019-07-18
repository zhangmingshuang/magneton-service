package com.magneton.service.core.spring.limiter;

import com.magneton.service.core.limiter.TimesLimiterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TimesLimiter的配置转化以支持SpringBoot的配置文件自动装配
 *
 * @author zhangmingshuang
 * @since 2019/6/21
 */
@ConfigurationProperties(
        prefix = TimesLimiterProperties.PREFIX, ignoreInvalidFields = true)
public class TimesLimiterProperties extends TimesLimiterConfig {
    public static final String PREFIX = "magneton.limiter.times";


    public static final String MODE_LOCAL = "local";
    public static final String MODE_DISTRIBUTED = "distributed";

    /**
     * 防御节点类型
     * local 默认的。表示使用{@link com.magneton.service.core.limiter.DefaultTimesLimiter}
     * distributed 表示使用 {@link RedisTimesLimiter} 此时需要项目引入RedisTemplate包，否则还是会使用local模式
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
