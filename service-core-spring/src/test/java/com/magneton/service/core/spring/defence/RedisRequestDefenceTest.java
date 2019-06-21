package com.magneton.service.core.spring.defence;

import com.magneton.service.core.defence.RequestDefence;
import com.magneton.service.core.defence.RequestDefenceConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisRequestDefenceTest {

    private RequestDefence defence;

    @Autowired
    private RedisTemplate redisTemplate;


    @Before
    public void before() {
        redisTemplate.opsForValue().set("redisRequestDefenceTest", "value", 3, TimeUnit.SECONDS);

        RequestDefenceConfig config = new RequestDefenceConfig();
        config.setTokenNum(3);
        config.setRefreshTime(10);
        defence = new RedisRequestDefence(redisTemplate);
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
}
