package com.magneton.service.core.util;

import java.util.Locale;

/**
 * @author zhangmingshuang
 * @since 2019/8/1
 */
public class TimesUtil {
    public static final String secondsPrettyShow(long seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = "0";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "%02d秒", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d分%02d秒", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%d天%02d分%02d秒", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }
}
