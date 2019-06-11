package com.magneton.service.core.spring.redis;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * 用来支持Redis多数据源配置，这是一个自动加载类
 * 在项目 service-starter-spring 中被自动扫描加载
 *
 * @author zhangmingshuang
 * @since 2019/3/5
 */
@Component
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@EnableConfigurationProperties(MultiRedisProperties.class)
public class MultiRedisAutoConfiguration {

    @Autowired
    private ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider;
    @Autowired
    private ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider;
    @Autowired
    private ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> builderCustomizers;

    public RedisTemplate<String, String> createRedisTemplate(LettuceRedisProperties redisProperties)
            throws UnknownHostException {
        RedisTemplate<String, String> redisTemplate = new StringRedisTemplate();
        MultiLettuceConnectionConfiguration lettuceConnectionConfiguration
                = new MultiLettuceConnectionConfiguration(redisProperties,
                sentinelConfigurationProvider,
                clusterConfigurationProvider,
                builderCustomizers);
        RedisConnectionFactory redisConnectionFactory
                = lettuceConnectionConfiguration.getRedisConnectionFactory();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public MultiRedisTemplateFactory multiRedisTemplateFactory(ApplicationContext applicationContext,
                                                               MultiRedisProperties multiRedisProperties,
                                                               MultiRedisAutoConfiguration multiRedisAutoConfiguration) {
        MultiRedisTemplateFactory factory = new MultiRedisTemplateFactory();
        Map<String, LettuceRedisProperties> configs = multiRedisProperties.getMulti();
        if (configs != null) {
            configs.forEach((name, properties) -> {
                try {
                    RedisTemplate redisTemplate
                            = multiRedisAutoConfiguration.createRedisTemplate(properties);
                    factory.register(name, redisTemplate);
                    if (applicationContext.containsBean(name)) {
                        Object bean = applicationContext.getBean(name);
                        if (bean.getClass().isAssignableFrom(RedisTemplate.class)) {
                            return;
                        }
                        throw new IllegalArgumentException("redisTemplate name " + name
                                + " already registered by " + bean.getClass());
                    }
                    ((ConfigurableApplicationContext) applicationContext).getBeanFactory()
                            .registerSingleton(name, redisTemplate);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return factory;
    }

}
