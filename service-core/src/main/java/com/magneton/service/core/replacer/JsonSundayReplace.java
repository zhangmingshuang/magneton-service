package com.magneton.service.core.replacer;

/**
 * Sunday字符串匹配算法
 *
 * @author zhangmingshuang
 * @since 2017/12/25 14:29
 */
public class JsonSundayReplace {

    public static String replace(String dest, String expression) {
        return replace(dest, null, expression);
    }

    /**
     * JSON匹配
     *
     * @param dest       文本
     * @param pattern    匹配哪个key
     * @param expression 表达式
     * @return String
     */
    public static String replace(String dest, String pattern, String expression) {
        if (dest == null || expression == null || expression.length() == 0) {
            return dest;
        }
        if (pattern == null) {
            return StringMaskReplace.getInstance().replace(dest, expression);
        }
        int destLen = dest.length();
        int patternLen = pattern.length();
        if (destLen < patternLen) {
            return dest;
        }
        char[] destChars = dest.toCharArray();
        if (destLen == patternLen && dest.equals(pattern)) {
            StringMaskReplace.getInstance().replace(destChars, 0, destLen, expression);
            return new String(dest);
        }
        char[] patternChars = pattern.toCharArray();
        //从0位开始
        int i = 0, j = 0, g = destLen - patternLen;
        //如果有足够的字符查找
        while (i <= g + j) {
            if (destChars[i] != patternChars[j]) {
                if (i == g + j) {
                    break;
                }
                char nextChar = destChars[i + patternLen - j];
                //查找在pattern中的k
                int k;
                for (k = patternLen - 1; k > 0; k--) {
                    if (patternChars[k] == nextChar) {
                        break;
                    }
                    if (k == 0) {
                        k = -1;
                    }
                }
                i = i + patternLen - k - j;
                j = 0;
            } else {
                if (j == patternLen - 1) {
                    int start = nextChar(destChars, '"', nextChar(destChars, '"', i)) + 1;
                    int end = nextChar(destChars, '"', start);
                    StringMaskReplace.getInstance().replace(destChars, start, end, expression);
                    i = i - j + 1;
                    j = 0;
                } else {
                    i++;
                    j++;
                }
            }
        }
        return new String(destChars);
    }

    private static final int nextChar(char[] chars, char symbol, int startIndex) {
        for (int l = chars.length; startIndex < l; ) {
            if (chars[++startIndex] == symbol) {
                return startIndex;
            }
        }
        return -1;
    }
}
