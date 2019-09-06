package com.magneton.service.core.util;

import org.junit.Test;

/**
 * @author zhangmingshuang
 * @since 2019/8/1
 */
public class TimesUtilTest {

    @Test
    public void test(){
        String r = TimesUtil.secondsPrettyShow(10);
        System.out.println(r);

        r = TimesUtil.secondsPrettyShow(800);
        System.out.println(r);

        r = TimesUtil.secondsPrettyShow(3700);
        System.out.println(r);

        r = TimesUtil.secondsPrettyShow(3700000);
        System.out.println(r);
    }
}
