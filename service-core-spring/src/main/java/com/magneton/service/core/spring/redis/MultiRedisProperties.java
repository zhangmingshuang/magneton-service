package com.magneton.service.core.spring.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@ConditionalOnClass(RedisTemplate.class)
@ConfigurationProperties(prefix = "spring.redis")
public class MultiRedisProperties {

    private Map<String, LettuceRedisProperties> multi;

    public void setMulti(Map<String, LettuceRedisProperties> multi) {
        this.multi = multi;
    }

    public Map<String, LettuceRedisProperties> getMulti() {
        return multi;
    }
}
