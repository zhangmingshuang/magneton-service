package com.magneton.service.core;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public class ResultCodeFactory {
    private static final String[] CODE_DESC = {"操作成功", "操作失败"};
    private static final int[] CODE = {0, 1};

    public static final String getSuccessCodeDesc() {
        return CODE_DESC[0];
    }

    public static final void setSuccessCodeDesc(String desc) {
        CODE_DESC[0] = desc;
    }

    public static final String getErrorCodeDesc() {
        return CODE_DESC[1];
    }

    public static final void setErrorCodeDesc(String desc) {
        CODE_DESC[1] = desc;
    }

    public static final void setSuccessCode(int code) {
        CODE[0] = code;
    }

    public static final void setErrorCode(int code) {
        CODE[1] = code;
    }

    public static final int getSuccessCode() {
        return CODE[0];
    }

    public static final int getErrorCode() {
        return CODE[1];
    }
}
