package com.magneton.service.core.defence;

import com.magneton.service.core.Configurable;

/**
 * 请求防御
 * 定义一个请求防御的概念
 * 假设用户有多个的令牌可以用来兼容处理错误，比如登录密码输入错误。
 * 当错误发生时，扣除对应的一个令牌。 如果在令牌的重置期间令牌不足扣除，则设定对应限制逻辑
 *
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public interface RequestDefence extends Configurable<RequestDefenceConfig> {

    enum Mode {
        LOCAL,
        DISTRIBUTED
    }

    /**
     * 获取防御的冷却时间， 如果时间大于0表示还处理防御机制中
     * 此时业务上应该不允许请求通过
     *
     * @param key Key
     * @return 如果该Key还处于防御时间，则返回需要等待的秒数，否则返回-1
     */
    long ttl(String key);

    /**
     * 扣除一个令牌数量
     *
     * @param key Key
     * @return 返回剩余可扣除的令牌数量，如果没有令牌可以扣除了返回0
     * 返回0同时也表示该Key已经进行了防御冷却
     */
    int decrAcquire(String key);

    /**
     * 删除令牌统计
     *
     * @param key Key
     */
    void remote(String key);

    /**
     * 返回模式信息
     *
     * @return {@link Mode}
     */
    Mode mode();
}
