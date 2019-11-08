package com.magneton.service.support.pubsub;

/**
 * @author zhangmingshuang
 * @since 2019/11/7
 */
public interface Pubsuber {

    void fire(String channel, String message);

    void fire(String[] channels, String message);
}
