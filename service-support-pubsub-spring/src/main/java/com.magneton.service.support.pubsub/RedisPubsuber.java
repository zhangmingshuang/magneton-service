package com.magneton.service.support.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhangmingshuang
 * @since 2019/11/7
 */
@Component
public class RedisPubsuber implements Pubsuber {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void fire(String channel, String message) {
        redisTemplate.execute(
            (RedisCallback) conn -> conn.publish(channel.getBytes(), message.getBytes()));
    }

    @Override
    public void fire(String[] channels, String message) {
        redisTemplate.execute((RedisCallback) conn -> {
            for (String channel : channels) {
                conn.publish(channel.getBytes(), message.getBytes());
            }
            return Boolean.TRUE;
        });
    }
}
