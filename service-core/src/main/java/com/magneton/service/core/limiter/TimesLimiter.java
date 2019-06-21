package com.magneton.service.core.limiter;

import com.magneton.service.core.Configurable;

/**
 * 频率次数限制器
 *
 * @author zhangmingshuang
 * @since 2019/6/20
 */
public interface TimesLimiter extends Configurable<TimesLimiterConfig> {

    default boolean increase(String key, String rule) {
        return this.increase(key, rule, 1);
    }

    boolean increase(String key, String rule, int incr);
}
