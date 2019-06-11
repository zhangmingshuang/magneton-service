package com.magneton.service.core.replacer;

/**
 * @author zhangmingshuang
 * @since 2018/1/9 23:35
 */
public class StringMasker {
    /**
     * 身份证掩码
     */
    public static final String IDNO_MASK = "#{3}*?#{4}";
    /**
     * 手机掩码
     */
    public static final String PHONE_MASK = "#{3}*?#{4}";
    /**
     * 邮箱掩码
     */
    public static final String EMAIL_MASK = "#{3}*?@";

    private StringBuilder excepisson = new StringBuilder();

    public static StringMasker create() {
        return new StringMasker();
    }

    public String build() {
        return excepisson.toString();
    }

    /**
     * 隐藏N个 *?
     *
     * @return StringMasker
     */
    public StringMasker hideN() {
        excepisson.append("*?");
        return this;
    }

    /**
     * 显示N个  #?
     *
     * @return StringMasker
     */
    public StringMasker showN() {
        excepisson.append("#?");
        return this;
    }

    /**
     * 隐藏
     *
     * @param len 长度
     * @return StringMasker
     */
    public StringMasker hide(int len) {
        excepisson.append("*{").append(len).append('}');
        return this;
    }

    /**
     * 显示
     *
     * @param len 长度
     * @return StringMasker
     */
    public StringMasker show(int len) {
        excepisson.append("#{").append(len).append('}');
        return this;
    }

    /**
     * 隐藏
     *
     * @return StringMasker
     */
    public StringMasker hide() {
        excepisson.append('*');
        return this;
    }

    /**
     * 显示
     *
     * @return StringMasker
     */
    public StringMasker show() {
        excepisson.append('#');
        return this;
    }

    /**
     * 添加截断字符
     *
     * @param charSequence 字符串
     * @return StringMasker
     */
    public StringMasker block(CharSequence charSequence) {
        excepisson.append(charSequence);
        return this;
    }
}
