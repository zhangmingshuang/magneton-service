package com.magneton.service.core.spring.defence;

import com.magneton.service.core.defence.GuavaCacheRequestDefence;
import com.magneton.service.core.defence.RequestDefence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhangmingshuang
 * @since 2019/6/4
 */
@Configuration
@EnableConfigurationProperties(RequestDefenceProperties.class)
public class RequestDefenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(RequestDefence.class)
    public RequestDefence requestDefence(RequestDefenceProperties properties) {
        RequestDefence defence = new GuavaCacheRequestDefence();
        defence.setRequestDefenceConfig(properties);
        return defence;
    }

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    public RequestDefence requestDefence(RedisTemplate redisTemplate,
                                         RequestDefenceProperties properties) {
        RequestDefence defence;
        if (properties.isLocal()) {
            defence = new GuavaCacheRequestDefence();
        } else {
            defence = new RedisRequestDefence(redisTemplate);
        }
        defence.setRequestDefenceConfig(properties);
        System.out.println("11111111:" + defence.mode());
        return defence;
    }
}
