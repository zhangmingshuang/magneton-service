package com.magneton.service.core.spring.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class MultiRedisTemplateFactory {
    private Map<String, RedisTemplate> redisTemplates
            = new ConcurrentHashMap<>(1);

    public void register(String name, RedisTemplate redisTemplate) {
        RedisTemplate exist
                = redisTemplates.putIfAbsent(name, redisTemplate);
        if (exist != null){
            throw new IllegalArgumentException(
                    "redisTemplate name " + name + " is already exists.");
        }
    }

    public RedisTemplate getRedisTemplate(String name){
        return redisTemplates.get(name);
    }
}
