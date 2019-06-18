package com.magneton.service.core.signuature;

import com.magneton.service.core.signature.TimeoutValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangmingshuang
 * @since 2019/6/14
 */
public class TimeoutValidatorTest {

    private TimeoutValidator timeoutValidator;

    @Before
    public void before() {
        timeoutValidator = new TimeoutValidator(3_000,
                10_000,
                new String[]{
                        "/abc",
                        "/123/123/abc"
                });
    }

    @Test
    public void testIsTimeOut() {
        long now = System.currentTimeMillis();
        Assert.assertFalse(timeoutValidator.isTimeOut(now));

        now -= 5 * 1000;
        Assert.assertTrue(timeoutValidator.isTimeOut(now));
    }

    @Test
    public void testIsAllowExclude() {
        long now = System.currentTimeMillis();
        Assert.assertFalse(timeoutValidator.isAllowExclude(now, "safasdf"));

        now -= 5 * 1000;
        Assert.assertTrue(timeoutValidator.isAllowExclude(now, "/abc"));

        now -= 5 * 1000;
        Assert.assertFalse(timeoutValidator.isAllowExclude(now, "/abc"));

        now -= 5 * 1000;
        Assert.assertFalse(timeoutValidator.isAllowExclude(now, "/abc"));
    }

    @Test
    public void testIsAllowExclude2() {
        TimeoutValidator timeoutValidator2 = new TimeoutValidator(3_000,
                -1,
                new String[]{
                        "/abc",
                        "/123/123/abc"
                });

        long now = System.currentTimeMillis();
        Assert.assertFalse(timeoutValidator2.isAllowExclude(now, "safasdf"));

        now -= 5 * 1000;
        Assert.assertTrue(timeoutValidator2.isAllowExclude(now, "/abc"));

        now -= 5 * 1000;
        Assert.assertTrue(timeoutValidator2.isAllowExclude(now, "/abc"));

        now -= 5 * 1000;
        Assert.assertTrue(timeoutValidator2.isAllowExclude(now, "/abc"));
    }

    @Test
    public void testGetTimeOutExcludePaths() {
        String[] paths = timeoutValidator.getTimeOutExcludePaths();
        paths[0] = "sssss";
        Assert.assertEquals(paths[0], "sssss");
        Assert.assertEquals(timeoutValidator.getTimeOutExcludePaths()[0],"/abc");
    }

}
