package com.magneton.service.core.defence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class GuavaCacheRequestDefenceTest {

    private GuavaCacheRequestDefence defence;

    @Before
    public void before() {
        RequestDefenceConfig config = new RequestDefenceConfig();
        config.setTokenNum(3);
        config.setRefreshTime(10);
        defence = new GuavaCacheRequestDefence();
        defence.setRequestDefenceConfig(config);
    }

    @Test
    public void testTtl() {
        long ttl = defence.ttl("unknow");
        Assert.assertTrue(ttl == -1);
    }

    @Test
    public void testDecrAcquire() {
        long tokenNum = defence.decrAcquire("abc2");
        Assert.assertTrue(tokenNum == 2);
    }

    @Test
    public void test() {
        String key = "testKey";
        long ttl = defence.ttl(key);
        Assert.assertTrue(ttl == -1);
        long tokenNum = defence.decrAcquire(key);
        Assert.assertTrue(tokenNum == 2);
        ttl = defence.ttl(key);
        Assert.assertTrue(ttl == -1);
        tokenNum = defence.decrAcquire(key);
        tokenNum = defence.decrAcquire(key);
        Assert.assertTrue(tokenNum == 0);
        ttl = defence.ttl(key);
        Assert.assertTrue(ttl > 0);
    }


}
