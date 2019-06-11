package com.magneton.service.core.defence;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class RequestDefenceConfig {
    /**
     * 令牌数量
     */
    private int tokenNum = 5;
    /**
     * 令牌冷却时间，单位秒
     * 当某个Key的令牌数量消耗完之后，需要等待该冷却时间之后
     * 才会重置令牌数量
     */
    private int refreshTime = 5 * 60;
    /**
     * 冷却时间防御，单位秒
     * 用来配置冷却时间的防御机制，如果当前是处于令牌冷却时间内
     * 用户的请求默认情况下，不会影响冷却时间。
     * 如果基于安全考虑，在冷却时间内如果再请求需要增加冷却时间的时候，
     * 配置该属性，则每次请求都在在剩余的冷却时间上增加该防御时间。
     */
    private int refreshDefineTime;

    public int getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(int tokenNum) {
        this.tokenNum = tokenNum;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public int getRefreshDefineTime() {
        return refreshDefineTime;
    }

    public void setRefreshDefineTime(int refreshDefineTime) {
        this.refreshDefineTime = refreshDefineTime;
    }
}
