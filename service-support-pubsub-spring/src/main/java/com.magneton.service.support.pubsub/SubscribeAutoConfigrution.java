package com.magneton.service.support.pubsub;

import com.magneton.service.core.spring.ApplicationContext;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhangmingshuang
 * @since 2019/3/6
 */
@Component
public class SubscribeAutoConfigrution {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeAutoConfigrution.class);

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发布处理器
     */
    @Autowired(required = false)
    private PublishEvent[] publishEvents;

    @PostConstruct
    public void subscribe() {
        this.registerMessageListener();
    }

    private void registerMessageListener() {
        if (publishEvents == null || publishEvents.length < 1) {
            return;
        }
        for (MessageListener messageListener : publishEvents) {
            Subscribe subscribe = messageListener.getClass().getAnnotation(Subscribe.class);
            if (subscribe == null) {
                continue;
            }
            String[] channels = subscribe.channels();
            if (channels == null || channels.length < 1) {
                continue;
            }
            RedisTemplate ref;
            String rt = subscribe.redisTemplate();
            if (rt == null || rt.isEmpty()) {
                ref = redisTemplate;
            } else {
                ref = ApplicationContext.getBean(rt, RedisTemplate.class);
                if (ref == null) {
                    throw new RuntimeException("找不到RedisTemplate,名称：" + rt);
                }
            }
            ref.execute((RedisCallback) conn -> {
                for (String channel : channels) {
                    LOGGER.info("Redis Subscribe. {} from {}", channel, messageListener.getClass());
                    conn.subscribe(messageListener, channel.getBytes());
                }
                return null;
            });
        }
    }
}
