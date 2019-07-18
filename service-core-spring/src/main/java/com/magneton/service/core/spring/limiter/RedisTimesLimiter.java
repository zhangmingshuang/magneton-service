package com.magneton.service.core.spring.limiter;

import com.magneton.service.core.limiter.LimiterRule;
import com.magneton.service.core.limiter.TimesLimiter;
import com.magneton.service.core.limiter.TimesLimiterConfig;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisTemplate的TimesLimiter
 * <p>
 * 功能与{@link com.magneton.service.core.limiter.DefaultTimesLimiter} 一致
 *
 * @author zhangmingshuang
 * @see TimesLimiterProperties
 * @see TimesLimiterConfiguration
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
    public int ttl(String key, String rule) {
        return (int) redisTemplate.execute((RedisCallback) conn -> {
            byte[] ks = (key + ":" + rule).getBytes();
            Long ttl = conn.ttl(ks);
            if (ttl == null || ttl.intValue() < 0) {
                return -1;
            }
            return ttl.intValue();
        });
    }

    @Override
    public int remain(String key, String rule) {
        return (int) redisTemplate.execute((RedisCallback) conn -> {
            byte[] ks = (key + ":" + rule).getBytes();
            byte[] value = conn.get(ks);
            if (value == null || value.length < 1) {
                LimiterRule limiterRule = config.getRules().get(rule);
                if (limiterRule == null && config.isForce()) {
                    LimiterRule defaultRule = config.getDefaultRule();
                    return defaultRule.getTimes();
                }
                if (limiterRule == null) {
                    return -1;
                }
                return limiterRule.getTimes();
            }
            return Long.parseLong(new String(value));
        });
    }

    @Override
    public int increaseEx(String key, String rule, int incr) {
        return (int) redisTemplate.execute((RedisCallback) conn -> {
            //判断的Key与规则不存在，获取规则
            LimiterRule limiterRule = config.getRules().get(rule);
            if (limiterRule == null) {
                if (!config.isForce()) {
                    return incr;
                }
                limiterRule = config.getDefaultRule();
            }
            byte[] ks = (key + ":" + rule).getBytes();
            Boolean exists = conn.exists(ks);
            if (exists == null || !exists.booleanValue()) {
                Boolean setNx = conn.setNX(ks, (limiterRule.getTimes() + "").getBytes());
                if (setNx != null && setNx.booleanValue()) {
                    //设置成功
                    conn.expire(ks, limiterRule.getExpireIn());
                }
            }
            Long remainTimes = conn.decrBy(ks, incr);
            if (remainTimes != null) {
                return (int) (limiterRule.getTimes() - remainTimes.longValue());
            }
            return incr;
        });
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
            Long remainTimes = conn.decrBy(ks, incr);
            if (remainTimes == null || remainTimes.longValue() < 0) {
                return false;
            }
            return true;
        });
    }


}
