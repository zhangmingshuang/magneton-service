package com.magneton.service.core;

/**
 * @author zhangmingshuang
 * @since 2019/6/21
 */
public interface Configurable<Config> {

    void afterConfigSet(Config config);
}
