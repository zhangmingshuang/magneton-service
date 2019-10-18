package com.magneton.service.core;

import com.magneton.service.core.util.StringUtil;

/**
 * @author zhangmingshuang
 * @since 2019/4/11
 */
public class ResultException extends RuntimeException {

    private int failCode;
    private String failMsg = "操作失败";

    public ResultException(String exceptionMsg) {
        super(exceptionMsg);
    }

    public ResultException(int failCode, String exceptionMsg) {
        super(exceptionMsg);
        this.failCode = failCode;
    }


    public ResultException(int failCode, String failMsg, String exceptionMsg) {
        super(exceptionMsg);
        this.failCode = failCode;
        this.failMsg = failMsg;
    }

    public ResultException(Throwable e) {
        super(e);
    }

    public Result result() {
        String msg = StringUtil.isEmpty(failMsg) ? "操作失败" : failMsg;
        return Result.fail(failMsg)
                .code(failCode < 1 ? 1 : failCode);
    }
}
