package com.magneton.service.core.signature;

/**
 * @author zhangmingshuang
 * @since 2019/6/14
 */
public class TimeoutValidator {
    private final long timeOut;
    private final long timeOutExclude;
    private final String[] timeOutExcludePaths;

    public TimeoutValidator(long timeOut, long timeOutExclude, String[] timeOutExcludePaths) {
        this.timeOut = timeOut;
        this.timeOutExclude = timeOutExclude;
        this.timeOutExcludePaths = timeOutExcludePaths;
    }

    public boolean isTimeOut(long time) {
        System.out.println(System.currentTimeMillis() - time);
        return System.currentTimeMillis() - time > timeOut;
    }

    public boolean isAllowExclude(long time, String uri) {
        if (timeOutExcludePaths == null || timeOutExcludePaths.length < 1) {
            return false;
        }
        for (String exclude : timeOutExcludePaths) {
            if (!exclude.equals(uri)) {
                continue;
            }
            if (timeOutExclude < 1) {
                return true;
            }
            return System.currentTimeMillis() - time < timeOutExclude;
        }
        return false;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public long getTimeOutExclude() {
        return timeOutExclude;
    }

    public String[] getTimeOutExcludePaths() {
        return timeOutExcludePaths.clone();
    }
}
