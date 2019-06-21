package com.magneton.service.core.spring.limiter;

import com.magneton.service.core.limiter.LimiterRule;
import com.magneton.service.core.limiter.TimesLimiter;
import com.magneton.service.core.limiter.TimesLimiterConfig;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhangmingshuang
 * @since 2019/6/21
 */
public class RedisTimesLimiter implements TimesLimiter {

    private RedisTemplate redisTemplate;

    private TimesLimiterConfig config;

    public RedisTimesLimiter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConfigSet(TimesLimiterConfig config) {
        this.config = config;
    }

    @Override
    public boolean increase(String key, String rule, int incr) {
        return (boolean) redisTemplate.execute((RedisCallback) conn -> {
            byte[] ks = (key + ":" + rule).getBytes();
            Boolean exists = conn.exists(ks);
            if (exists == null || !exists.booleanValue()) {
                //判断的Key与规则不存在，获取规则
                LimiterRule limiterRule = config.getRules().get(rule);
                if (limiterRule == null) {
                    if (!config.isForce()) {
                        return true;
                    }
                    limiterRule = config.getDefaultRule();
                }
                Boolean setNx = conn.setNX(ks, (limiterRule.getTimes() + "").getBytes());
                if (setNx != null && setNx.booleanValue()) {
                    //设置成功
                    conn.expire(ks, limiterRule.getExpireIn());
                }
            }
            Long remainTimes = conn.decrBy(ks, 1);
            if (remainTimes == null || remainTimes.longValue() < 0) {
                return false;
            }
            return true;
        });
    }


}
