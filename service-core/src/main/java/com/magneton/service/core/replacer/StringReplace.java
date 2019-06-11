package com.magneton.service.core.replacer;

/**
 * @author zhangmingshuang
 * @since 2017/12/25 10:16
 */
public interface StringReplace {
    /**
     * 字符串替换
     * @param str           原始串
     * @param expressionStr 表达式
     * @return 替换后的字符串
     */
    String replace(String str, String expressionStr);

    /**
     * 字符数组替换
     * @param destStr       原始字符会数组
     * @param start         从哪个位置开始
     * @param end           到哪个位置结束
     * @param expressionStr 表达式
     */
    public void replace(char[] destStr, int start, int end, String expressionStr);
}
