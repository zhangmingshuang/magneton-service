package com.magneton.service.core.limiter;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangmingshuang
 * @since 2019/6/20
 */
@Setter
@Getter
public class LimiterRule {
    /**
     * 规则次数
     * 在时间范围内，允许的请求次数
     */
    private int times = 3600;
    /**
     * 到期时间，单位秒
     * 如设置86400，表示该规则的name在规则创建时之后的86400秒后过期
     */
    private int expireIn = 3600;
}