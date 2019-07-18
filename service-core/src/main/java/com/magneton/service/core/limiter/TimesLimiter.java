package com.magneton.service.core.limiter;

import com.magneton.service.core.Configurable;

/**
 * 频率次数限制器
 * 这是一个规则可配置规则的次数限制器
 * 主要用来解决某个Key在某个场景中需要限制在一定时间范围内的次数
 * 比如，发送短信服务
 * 在1分钟内限制发送1次，在1小时内限制发送5次的业务场景
 * 配置如下：
 * <pre>
 * minute:
 *   times: 1
 *   expireIn: 60
 * hour:
 *   times: 5
 *   expireIn: 3600
 * </pre>
 * 更祥细的配置，请查看{@link TimesLimiterConfig}配置说明
 * <p>
 * 使用示例请查看默认实现类 {@link DefaultTimesLimiter}
 *
 * @author zhangmingshuang
 * @since 2019/6/20
 */
public interface TimesLimiter extends Configurable<TimesLimiterConfig> {

    /**
     * 增加一次统计
     *
     * @param key  Key
     * @param rule 规则
     * @return 是否增加成功
     */
    default boolean increase(String key, String rule) {
        return this.increase(key, rule, 1);
    }

    /**
     * 增加统计
     *
     * @param key  Key
     * @param rule 规则
     * @param incr 增加的次数
     * @return 是否增加成功
     */
    boolean increase(String key, String rule, int incr);

    /**
     * 直接增加统计
     * 但是不对统计的结果进行校验
     * 也就是不管是否超过限制的次数都不做逻辑操作
     *
     * @param key  Key
     * @param rule 规则
     * @return 返回增加之后的次数
     */
    default int increaseEx(String key, String rule) {
        return this.increaseEx(key, rule, 1);
    }

    int increaseEx(String key, String rule, int incr);

    /**
     * 取得剩余下的可使用次数
     *
     * @param key  Key
     * @param rule 规则
     * @return 剩余的次数
     * 返回0表示没有剩余的次数
     * 如果返回-1表示没有限制
     */
    int remain(String key, String rule);

    /**
     * 取得当前Key在多少秒之后过期
     *
     * @param key  Key
     * @param rule 规则
     * @return 在多少秒之后过期
     * 如果规则或者key还未被创建，返回-1
     */
    int ttl(String key, String rule);
}
