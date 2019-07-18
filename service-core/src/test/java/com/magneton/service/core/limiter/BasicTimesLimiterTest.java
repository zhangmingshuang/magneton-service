package com.magneton.service.core.limiter;

import org.junit.Assert;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/7/18
 */
public class BasicTimesLimiterTest {

    public TimesLimiter createDefaultTimesLimiter(String ruleName) {
        Map<String, LimiterRule> rules = new HashMap<>();
        LimiterRule secondsLimiterRule = new LimiterRule();
        //3s
        secondsLimiterRule.setExpireIn(3);
        //3times
        secondsLimiterRule.setTimes(3);
        rules.put(ruleName, secondsLimiterRule);

        TimesLimiterConfig remainConfig = new TimesLimiterConfig();
        remainConfig.setRules(rules);
        remainConfig.setDefaultRule(secondsLimiterRule);

        TimesLimiter limiter = new DefaultTimesLimiter();
        limiter.afterConfigSet(remainConfig);
        return limiter;
    }

    protected void testRemain(String rule, TimesLimiter limiter) {
        int remain = limiter.remain("testKey", rule);
        Assert.assertTrue(remain == 3);
        remain = limiter.remain("testKey2", rule);
        Assert.assertTrue(remain == 3);
        remain = limiter.remain("testKey", rule + "2");
        Assert.assertTrue(remain == 3);
        remain = limiter.remain("testKey2", rule + "2");
        Assert.assertTrue(remain == 3);

        limiter.increase("testKey", rule);
        remain = limiter.remain("testKey", rule);
        Assert.assertTrue(remain == 2);

        limiter.increase("testKey", rule + "2");
        remain = limiter.remain("testKey", rule + "2");
        Assert.assertTrue(remain == -1);
    }

    protected void testIncreaseEx(String testIncreaseEx, TimesLimiter limiter) {
        int incr = limiter.increaseEx("test", testIncreaseEx, 4);
        System.out.println(incr);
        Assert.assertFalse(limiter.increase("test", testIncreaseEx));

        incr = limiter.increaseEx("test", testIncreaseEx + "2", 4);
        System.out.println(incr);
        Assert.assertFalse(limiter.increase("test", testIncreaseEx + "2"));
    }

    protected void testTtl(String testTtl, TimesLimiter limiter) {
        Assert.assertEquals(limiter.ttl("test", testTtl), -1);
        limiter.increase("test", testTtl);
        Assert.assertTrue(limiter.ttl("test", testTtl) >= 2);
    }

    protected void testRemainWithArray(String testRemainWithArray, TimesLimiter limiter) {
        int[] tests = limiter.remain("test",
                testRemainWithArray,
                testRemainWithArray + "2",
                testRemainWithArray + "3");
        Assert.assertTrue(Arrays.equals(tests, new int[]{0, -1, -1}));
        limiter.increase("test",testRemainWithArray + "2");

        tests = limiter.remain("test",
                testRemainWithArray,
                testRemainWithArray + "2",
                testRemainWithArray + "3");
        System.out.println(Arrays.toString(tests));
    }
}
