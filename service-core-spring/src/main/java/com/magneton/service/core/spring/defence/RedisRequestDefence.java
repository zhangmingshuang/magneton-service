package com.magneton.service.core.spring.defence;

import com.magneton.service.core.defence.RequestDefence;
import com.magneton.service.core.defence.RequestDefenceConfig;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class RedisRequestDefence implements RequestDefence {

    private RedisTemplate redisTemplate;
    private RequestDefenceConfig config;

    public RedisRequestDefence(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConfigSet(RequestDefenceConfig config) {
        this.config = config;
    }

    @Override
    public long ttl(String key) {
        byte[] k = key.getBytes();
        return (long) redisTemplate.execute((RedisCallback) conn -> {
            byte[] v = conn.get(k);
            if (v == null || v.length < 1) {
                return -1L;
            }
            Long l = Long.parseLong(new String(v));
            if (l.longValue() >= config.getTokenNum()) {
                //超过Token限制
                long ttl = conn.ttl(k);
                if (ttl < 0) {
                    return -1L;
                }
                long time = config.getRefreshDefineTime() + ttl;
                conn.expire(k, time);
                return time;
            }
            return -1L;
        });
    }

    @Override
    public void remote(String key) {
        byte[] k = key.getBytes();
        redisTemplate.execute((RedisCallback) conn -> conn.del(k));
    }

    @Override
    public int decrAcquire(String key) {
        byte[] k = key.getBytes();
        long num = (long) redisTemplate.execute((RedisCallback) conn -> {
            Long n = conn.incrBy(k, 1);
            conn.expire(k, config.getRefreshTime());
            return n == null ? 0 : n.longValue();
        });
        if (num > config.getTokenNum()) {
            return 0;
        }
        return (int) (config.getTokenNum() - num);
    }

    @Override
    public Mode mode() {
        return Mode.DISTRIBUTED;
    }
}
