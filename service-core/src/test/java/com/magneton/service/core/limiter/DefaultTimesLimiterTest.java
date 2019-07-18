package com.magneton.service.core.limiter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangmingshuang
 * @since 2019/6/21
 */
public class DefaultTimesLimiterTest {

    private TimesLimiterConfig config;
    private String seconds = "seconds";

    @Before
    public void before() {
        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(seconds, secondsLimiterRule);

        config = new TimesLimiterConfig();
        config.setRules(rules);
    }

    @Test
    public void testTtl() {

        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put("remain", secondsLimiterRule);

        TimesLimiterConfig remainConfig = new TimesLimiterConfig();
        remainConfig.setRules(rules);
        remainConfig.setDefaultRule(secondsLimiterRule);

        TimesLimiter limiter = new DefaultTimesLimiter();
        limiter.afterConfigSet(remainConfig);

        Assert.assertEquals(limiter.ttl("test", "remain"), -1);
        limiter.increase("test", "remain");
        Assert.assertTrue(limiter.ttl("test", "remain") >= 2);
    }

    @Test
    public void testRemain() {
        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put("remain", secondsLimiterRule);

        TimesLimiterConfig remainConfig = new TimesLimiterConfig();
        remainConfig.setRules(rules);
        remainConfig.setDefaultRule(secondsLimiterRule);

        TimesLimiter limiter = new DefaultTimesLimiter();
        limiter.afterConfigSet(remainConfig);
        int remain = limiter.remain("testKey", "remain");
        Assert.assertTrue(remain == 3);
        remain = limiter.remain("testKey2", "remain");
        Assert.assertTrue(remain == 3);
        remain = limiter.remain("testKey", "remain2");
        Assert.assertTrue(remain == 3);
        remain = limiter.remain("testKey2", "remain2");
        Assert.assertTrue(remain == 3);

        limiter.increase("testKey", "remain");
        remain = limiter.remain("testKey", "remain");
        Assert.assertTrue(remain == 2);

        limiter.increase("testKey", "remain2");
        remain = limiter.remain("testKey", "remain2");
        Assert.assertTrue(remain == -1);
    }

    @Test
    public void test4() {

        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(seconds, secondsLimiterRule);

        TimesLimiterConfig config3 = new TimesLimiterConfig();
        config3.setRules(rules);
        config3.setForce(true);

        LimiterRule defaultRule = new LimiterRule();
        defaultRule.setTimes(1);
        defaultRule.setExpireIn(3);
        config3.setDefaultRule(defaultRule);

        DefaultTimesLimiter defaultTimesLimiter = new DefaultTimesLimiter();
        defaultTimesLimiter.afterConfigSet(config3);

        int t = 1000;
        CountDownLatch cdl = new CountDownLatch(t);
        AtomicLong error = new AtomicLong();
        for (int i = 0; i < t; i++) {
            new Thread() {
                @Override
                public void run() {
                    boolean r = defaultTimesLimiter.increase("aaaa", "notRule");
                    if (!r) {
                        error.incrementAndGet();
                    }
                    cdl.countDown();
                }
            }.start();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("失败：" + error);
        Assert.assertEquals(error.intValue(), t - 1);

    }

    @Test
    public void test3() {
        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(seconds, secondsLimiterRule);

        TimesLimiterConfig config3 = new TimesLimiterConfig();
        config3.setRules(rules);
        config3.setForce(true);

        LimiterRule defaultRule = new LimiterRule();
        defaultRule.setTimes(1);
        defaultRule.setExpireIn(3);
        config3.setDefaultRule(defaultRule);

        DefaultTimesLimiter defaultTimesLimiter = new DefaultTimesLimiter();
        defaultTimesLimiter.afterConfigSet(config3);

        boolean res = defaultTimesLimiter.increase("testKey", "notRule");
        Assert.assertTrue(res);

        res = defaultTimesLimiter.increase("testKey", "notRule");
        Assert.assertFalse(res);
    }

    @Test
    public void test2() {
        DefaultTimesLimiter defaultTimesLimiter = new DefaultTimesLimiter();
        defaultTimesLimiter.afterConfigSet(config);
        int t = 1000;
        CountDownLatch cdl = new CountDownLatch(t);
        AtomicLong error = new AtomicLong();
        for (int i = 0; i < t; i++) {
            new Thread() {
                @Override
                public void run() {
                    boolean r = defaultTimesLimiter.increase("test", seconds);
                    if (!r) {
                        error.incrementAndGet();
                    }
                    cdl.countDown();
                }
            }.start();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("失败：" + error);
        Assert.assertEquals(error.intValue(), t - 3);
    }

    @Test
    public void test1() {
        DefaultTimesLimiter defaultTimesLimiter = new DefaultTimesLimiter();
        defaultTimesLimiter.afterConfigSet(config);

        boolean res = defaultTimesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);

        res = defaultTimesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);

        res = defaultTimesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);

        res = defaultTimesLimiter.increase("testKey", seconds);
        Assert.assertFalse(res);
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = defaultTimesLimiter.increase("testKey", seconds);
        Assert.assertTrue(res);
    }
}
