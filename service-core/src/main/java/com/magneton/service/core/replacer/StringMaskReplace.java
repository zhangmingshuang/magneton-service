package com.magneton.service.core.replacer;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据过滤表达式：
 * <p>
 * {@code #} 表示该位显示，默认位
 * 比如： {@code ##} 即字符串 abcdefg 会全显示，因为默认{@code #}
 * <p>
 * 表示该位隐藏
 * 比如： {@code ##*#} 即字符串 abcdefg 会显示为：ab*defg
 * <p>
 * {@code {x}} 前置必须是{@code #}或{@code *}， 即{@code #{x}}, {@code *{x}}的组合，x为数字
 * 比如：{@code #{3}*} 表示显示前3，隐藏第4位
 * 如字符串： abcdefg 显示 abc*efg
 * <p>
 * {@code ?}   前置必须是{@code #}或{@code *}， 即{@code #？}, {@code *？}的组合
 * 表示0-N的{@code #}或{@code *}， N由字符串长度决定
 * 因为{@code #}或{@code *}是必须长度，则当字符串长度{@code <=#*}的长度时，将不会分配字符给{@code ?}来显示
 * 比如： {@code #?**} 表示必须隐藏2位，如果长度大于2位，则前置全显示
 * 如字符串：abcdefg 显示abcde**
 * 比如： {@code #{2}*?#{4}}即表示 前2后4显示，中间隐藏
 * 比如： {@code ##*?##*?#} 表示显示前2，中2，后1.如果还有足够的位数隐藏
 * 如字符串：abcdefg 显示ab*de*g.
 * {@code ?}的分配为后贪婪，即前{@code ?}数量{@code <=}后{@code ?}数量。 但是，前{@code ?}为优先分配
 * <p>
 * {@code ??} 前置必须是{@code #}或 {@code *}， 即{@code #??}或{@code *??}的组合，表示0-N 但是为渴望的
 * 即会优先分配完所有的可预算的显示或隐藏的字符
 * 比如： {@code abcd #?*??#?} 会显示成：a**d
 * 比如： {@code abcdef #?*??#?} 会显示成 a****f
 * <p>
 * 任意字符 表示截断
 * 截断规则 ： 截断分为前截断字符（B)与后截断字符(A)
 * 第1个截断字符: 后截断字符 = 第1个截断字符
 * 第2个截断字符: 前截断字符 = 后截断字符， 后截断字符=第2个截断字符
 * 第3个截断字符：前截断字符 = 后截断字符， 后截断字符=第3个截断字符
 * 即： {@code A = 断字符 -> B = A , A= 断字符 }....
 * <p>
 * 注意： 任何字符如果出现2次会表现从哪开始到哪结果的截断
 * 比如： {@code #**ab*} 则会从a开始（不包含） 到b结束（不包含）
 * <p>
 * 比如： {@code #?*{4}@} 表示在{@code @}之前的字符串隐藏4位
 * 如字符串：{@code abcdefg@email.com} 显示{@code abc****@eamil.com}
 * <p>
 * 实例：
 * 身份证：前3后4 {@code #{3}*?#{4}}
 * 手机号：前3  {@code #{3}*?}
 * 邮箱：  显示@之前的前3  {@code #{3}*?@}
 *
 * @author zhangmingshuang
 * @since 2017/12/22 21:56
 */
public class StringMaskReplace implements StringReplace {

    private static class Single {
        private static final StringReplace DATA_PASER = new StringMaskReplace();
    }

    public static StringReplace getInstance() {
        return Single.DATA_PASER;
    }

    /**
     * 掩码
     */
    private static final byte MASK = '*';
    private static final byte MASK_FLAG = 0B0000_0001;
    /**
     * 显示码
     */
    private static final byte SHOW = '#';
    private static final byte SHOW_FLAG = 0B0000_0010;
    /**
     * 数据表达开始
     */
    private static final byte NUM_START = '{';
    private static final byte NUM_START_FLAG = 0B0000_0100;
    /**
     * 数据表达结束
     */
    private static final byte NUM_END = '}';
    private static final byte NUM_END_FLAG = 0B0000_1000;
    /**
     * 数据0-N表示
     */
    private static final byte NUM_N = '?';
    private static final byte NUM_N_FLAG = 0B0001_0000;
    /**
     * {@code +} 表示后续全部#
     */
    private static final byte ALL_SHOW = '+';
    /**
     * {@code @} 表示未知#
     */
    private static final byte N_SHOW = '@';
    /**
     * {@code -} 表示后续全部{@code *}
     */
    private static final byte ALL_MASK = '-';
    /**
     * {@code &} 表示未知 {@code *}
     */
    private static final byte N_MASK = '&';
    /**
     * 表示未知的{@code #} 但是为多{@code #}，即要求多分配。是渴忘的
     * 所以，会将所有的剩余的可分配的全部分配（扣除必须的之外）
     */
    private static final byte NN_SHOW = ']';
    /**
     * 表示未知的{@code *} 但是为多{@code *}，即要求多分配。是渴忘的
     * 所以，会将所有的剩余的可分配的全部分配（扣除必须的之外）
     */
    private static final byte NN_MASK = '[';
    /**
     * 标识 后截断
     */
    private static final byte END_BLOCK_FLAG = 0B0010_0000;

    private StringMaskReplace() {

    }

    private static class Expression {
        protected char[] expression;
        protected int unnotNum;
        protected int min;
        protected char startChar;
        protected char endChar;
    }

    private static final Map<String, Expression> EXPRESSION_MAP = new ConcurrentHashMap<>();

    public static Expression parseExpression(String expression) {
        Expression expressionCached = EXPRESSION_MAP.get(expression);
        if (expressionCached != null) {
            return expressionCached;
        }
        expressionCached = new Expression();
        char[] chars = expression.toCharArray();
        char b;
        int expreLen = 16 << 1;
        int expreIndex = 0;
        char[] expre = new char[expreLen];
        byte flag = 0B0000_0000;
        int num = 0;
        int unnotNum = 0;
        //最少需要多少个元素
        int minLen = 0;
        //开始与结束的位置
        int s = 0, e = 0;
        try {
            for (int i = 0, l = chars.length; i < l; i++) {
                if (expreIndex >= expreLen) {
                    expre = Arrays.copyOf(expre, (expreLen = expreIndex + (expreIndex >> 1)));
                }
                b = chars[i];
                switch (b) {
                    case MASK:
                        flag |= MASK_FLAG;
                        flag &= ~SHOW_FLAG;
                        expre[expreIndex++] = MASK;
                        if ((flag & END_BLOCK_FLAG) != END_BLOCK_FLAG) {
                            minLen++;
                        }
                        break;
                    case SHOW:
                        flag |= SHOW_FLAG;
                        flag &= ~MASK_FLAG;
                        expre[expreIndex++] = SHOW;
                        if ((flag & END_BLOCK_FLAG) != END_BLOCK_FLAG) {
                            minLen++;
                        }
                        break;
                    case NUM_START:
                        //数据开始{
                        flag |= NUM_START_FLAG;
                        break;
                    case NUM_END:
                        //数字结束}
                        b = expre[expreIndex - 1];
                        if (expreIndex + num > expreLen) {
                            expreLen = expreLen + (expreIndex + num) + (expreLen << 1);
                            expre = Arrays.copyOf(expre, expreLen);
                        }
                        for (; num > 1; num--) {
                            expre[expreIndex++] = b;
                            if ((flag & END_BLOCK_FLAG) != END_BLOCK_FLAG) {
                                minLen++;
                            }
                        }
                        flag &= ~NUM_START_FLAG;
                        num = 0;
                        break;
                    case NUM_N:
                        //未知数量?
                        if (i + 1 < l && chars[i + 1] == NUM_N) {
                            //渴望的
                            if ((flag & MASK_FLAG) == MASK_FLAG) {
                                expre[expreIndex - 1] = NN_MASK;
                            } else if ((flag & SHOW_FLAG) == SHOW_FLAG) {
                                expre[expreIndex - 1] = NN_SHOW;
                            }
                            i++;
                            unnotNum++;
                            if ((flag & END_BLOCK_FLAG) != END_BLOCK_FLAG) {
                                minLen--;
                            }
                            break;
                        }
                        if (i == l - 1) {
                            //最后一个为？
                            if ((flag & MASK_FLAG) == MASK_FLAG) {
                                expre[expreIndex - 1] = ALL_MASK;
                            } else if ((flag & SHOW_FLAG) == SHOW_FLAG) {
                                expre[expreIndex - 1] = ALL_SHOW;
                            }
                            if ((flag & END_BLOCK_FLAG) != END_BLOCK_FLAG) {
                                minLen--;
                            }
                            unnotNum++;
                            break;
                        }
                        flag |= NUM_N_FLAG;
                        if ((flag & MASK_FLAG) == MASK_FLAG) {
                            expre[expreIndex - 1] = N_MASK;
                        } else if ((flag & SHOW_FLAG) == SHOW_FLAG) {
                            expre[expreIndex - 1] = N_SHOW;
                        }
                        unnotNum++;
                        if ((flag & END_BLOCK_FLAG) != END_BLOCK_FLAG) {
                            minLen--;
                        }
                        break;
                    default:
                        if ((flag & NUM_START_FLAG) == NUM_START_FLAG) {
                            num = num * 10 + (chars[i] & ~0B0011_0000);
                        } else {
                            if (expressionCached.endChar != 0) {
                                expressionCached.startChar = expressionCached.endChar;
                                s++;
                                minLen = 0;
                            }
                            expressionCached.endChar = b;
                            e = expreIndex;
                            flag &= END_BLOCK_FLAG;
                        }

                }
            }
            expressionCached.expression = Arrays.copyOfRange(expre, s, e == 0 ? expreIndex : e);
            expressionCached.unnotNum = unnotNum;
            expressionCached.min = minLen;
            EXPRESSION_MAP.putIfAbsent(expression, expressionCached);
            return expressionCached;
        } finally {
            expre = null;
            chars = null;
        }
    }

    @Override
    public void replace(char[] destStr, int start, int end, String expressionStr) {
        if (start == -1 || end == -1 || destStr == null || expressionStr == null
                || destStr.length == 0 || expressionStr.length() == 0) {
            return;
        }
        Expression expression = parseExpression(expressionStr);
        if (expression.endChar - expression.startChar < 0) {
            //开始即结束
            expression.endChar = expression.startChar;
            expression.startChar = 0;
        }

        if (expression.startChar != 0) {
            for (; start < end; ) {
                if (destStr[start++] == expression.startChar) {
                    break;
                }
            }
        }
        if (expression.endChar != 0) {
            for (; end > start; ) {
                if (destStr[--end] == expression.endChar) {
                    break;
                }
            }
        }
        int len = end - start;
        int strIndex = start;

        char[] expressionBytes = expression.expression;
        int min = expression.min;
        //可分配到N的数量
        int n = len > min ? len - min : 0;
        //需要分配几个N
        int nNum = expression.unnotNum;

        char b;
        for (int i = 0, l = expressionBytes.length; i < l; i++) {
            if (i >= len) {
                break;
            }
            b = expressionBytes[i];
            switch (b) {
                case MASK:
                    destStr[strIndex++] = MASK;
                    break;
                case SHOW:
                    strIndex++;
                    break;
                case N_MASK:
                    //N个掩码
                    if (nNum == 1) {
                        //全部分配
                        for (; n > 0; n--) {
                            destStr[strIndex++] = MASK;
                        }
                    } else {
                        int nowN = n / nNum--;
                        n -= nowN;
                        for (; nowN > 0; nowN--) {
                            destStr[strIndex++] = MASK;
                        }
                    }
                    break;
                case NN_MASK:
                    //渴望的隐藏，计算还剩余多少个未分配，此时该分配会分配最多
                    if (nNum == 1) {
                        //还剩下一个可分配，全部分配
                        for (; n > 0; n--) {
                            destStr[strIndex++] = MASK;
                        }
                    } else {
                        //判断还有多少个要分配
                        int nowN = n - --nNum;
                        for (; nowN > 0; nowN--) {
                            destStr[strIndex++] = MASK;
                        }
                        n -= nowN;
                    }
                    break;
                case N_SHOW:
                    if (nNum > 1) {
                        int nowN = n / nNum--;
                        n -= nowN;
                        strIndex += nowN;
                    } else {
                        strIndex += n;
                        n = 0;
                    }
                    break;
                case NN_SHOW:
                    if (nNum == 1) {
                        //全显示
                        strIndex += n;
                        n = 0;
                    } else {
                        int nowN = n - --nNum;
                        n -= nowN;
                        strIndex += nowN;
                    }
                    break;
                case ALL_MASK:
                    for (; strIndex < len; strIndex++) {
                        destStr[strIndex] = MASK;
                    }
                    break;
                case ALL_SHOW:
                    break;
                default:
                    //不会执行到这里
            }
        }
    }

    @Override
    public String replace(String str, String expressionStr) {
        if (str == null || expressionStr == null
                || str.length() == 0 || expressionStr.length() == 0) {
            return str;
        }
        char[] strBytes = str.toCharArray();
        replace(strBytes, 0, str.length(), expressionStr);
        return new String(strBytes);
    }

}
