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
        defence.afterConfigSet(config);
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
    public void testRemove() {
        long tokenNum = defence.decrAcquire("abc3");
        tokenNum = defence.decrAcquire("abc3");
        Assert.assertTrue(tokenNum == 1);
        defence.remote("abc3");
        tokenNum = defence.decrAcquire("abc3");
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

    @Test
    public void testFlow() {
        String userId = "userId";
        long ttl = defence.ttl(userId);
        if (ttl > 0) {
            System.out.println("请等待多少" + ttl + "秒后重试");
            return;
        }
        String pwd = "test";
        if (!"asb".equals(pwd)) {
            int i = defence.decrAcquire(userId);
            if (i <= 0) {
                System.out.println("您错误次数过多，已被锁定");
                return;
            }
            System.out.println("您还可以错误" + i + "次");
            return;
        }
        defence.remote(userId);

    }

}
