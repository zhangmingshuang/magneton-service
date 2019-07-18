package com.magneton.service.core.spring.limiter;

import com.magneton.service.core.limiter.DefaultTimesLimiter;
import com.magneton.service.core.limiter.TimesLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * SpringBoot的服务配置注主
 * 实现环境自动判断使用合适的TimesLimiter
 *
 * @author zhangmingshuang
 * @since 2019/6/4
 */
@Configuration
@EnableConfigurationProperties(TimesLimiterProperties.class)
public class TimesLimiterConfiguration {

    @Bean
    @ConditionalOnMissingBean(TimesLimiter.class)
    public TimesLimiter timesLimiter(TimesLimiterProperties properties) {
        TimesLimiter timesLimiter = new DefaultTimesLimiter();
        timesLimiter.afterConfigSet(properties);
        return timesLimiter;
    }

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    public TimesLimiter timesLimiter(RedisTemplate redisTemplate,
                                     TimesLimiterProperties properties) {
        TimesLimiter timesLimiter;
        if (properties.isLocal()) {
            timesLimiter = new DefaultTimesLimiter();
        } else {
            timesLimiter = new RedisTimesLimiter(redisTemplate);
        }
        timesLimiter.afterConfigSet(properties);
        return timesLimiter;
    }
}
