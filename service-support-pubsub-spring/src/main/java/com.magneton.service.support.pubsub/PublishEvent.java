package com.magneton.service.support.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author zhangmingshuang
 * @since 2019/5/8
 */
public interface PublishEvent extends MessageListener {

    void handle(String message);


    @Override
    default void onMessage(Message message, byte[] pattern) {
        this.handle(new String(message.getBody()));
    }
}
