package com.magneton.service.core.limiter;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/6/20
 */
@Setter
@Getter
public class TimesLimiterConfig {
    /**
     * 设置限制的规则
     * 如：
     * <pre>
     *     day:
     *      times: 3600
     *      expireIn: 3600
     *      consistency: false
     * </pre>
     * 表示配置了一个规则名称为day，限制为在3600秒内只允许请求3600次
     */
    private Map<String, LimiterRule> rules = new HashMap<>();
    /**
     * 强制规则匹配
     * 如果设置为true，则在调用方法时，强制判断规则rule是否存在于规则配置中
     * 如果不存在，不进行验证校验
     * <p>
     * 如果设置为false，则在调用方法进，判断规则rule如果不存在于规则配置表中
     * 那么使用默认的规则配置进行校验
     */
    private boolean force = false;
    /**
     * 默认规则设置
     */
    private LimiterRule defaultRule = new LimiterRule();


    public void addRule(String rule, LimiterRule limiterRule) {
        this.rules.put(rule, limiterRule);
    }
}
