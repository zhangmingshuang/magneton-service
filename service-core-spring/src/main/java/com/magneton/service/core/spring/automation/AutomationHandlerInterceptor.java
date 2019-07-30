package com.magneton.service.core.spring.automation;

import com.magneton.service.core.util.StringUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author zhangmingshuang
 * @since 2019/2/13 14:16
 * 扩展Spring {@link HandlerInterceptor} 使其可以自动被配置，
 * 而不需要再重写 {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer}实现
 * 并支持 {@link Order} 配置拦截顺序，顺序由大到小
 */
public interface AutomationHandlerInterceptor extends HandlerInterceptor {

    /**
     * 拦截器要拦截的地址规则
     *
     * @return 拦截地址
     */
    default String[] pathPatterns() {
        return new String[]{pathPatten()};
    }

    /**
     * 拦截器要拦截的地址规则
     *
     * @return 拦截地址
     */
    String pathPatten();

    /**
     * 配置拦截器需要过滤的地址规则
     *
     * @return 过滤地址
     */
    default String[] excludePathPatterns() {
        String excludePathPattern = excludePathPattern();
        if (excludePathPattern == null) {
            return null;
        }
        return new String[]{excludePathPattern()};
    }

    /**
     * 配置拦截器需要过滤的地址规则
     *
     * @return 过滤地址
     */
    default String excludePathPattern() {
        return null;
    }

    /**
     * 取得排序值
     *
     * @return 排序值
     */
    default int getOrder() {
        Order order = this.getClass().getAnnotation(Order.class);
        return order == null ? 0 : order.value();
    }

    default String[] pathResolver(String[] paths) {
        String[] resolvedPaths = new String[paths.length];
        for (int i = 0, l = paths.length; i < l; ++i) {
            resolvedPaths[i] = this.pathResolver(paths[i]);
        }
        return resolvedPaths;
    }

    /**
     * 地址解析，保证地址一定为：/??/**的标准拦截址
     *
     * @param path 要处理的地址
     * @return /??/**格式地址
     */
    default String pathResolver(String path) {
        if (StringUtil.isBlank(path)) {
            throw new IllegalArgumentException("错误的地址配置 " + path);
        }
        char[] chars = path.toCharArray();
        StringBuilder builder = new StringBuilder();
        char c = chars[0];
        if (c != '/') {
            builder.append('/').append(c);
        } else {
            builder.append(c);
        }
        for (int i = 1, l = chars.length; i < l; ++i) {
            c = chars[i];
            switch (c) {
                case '/':
                    if (chars[i - 1] == '/') {
                        //过滤处理//的地址符
                        break;
                    }
                    builder.append(c);
                    break;
                case '*':
                    if (i == chars.length - 1 && chars[i - 1] != '*') {
                        //结束尾是否为**尾
                        builder.append('*');
                    }
                    builder.append(c);
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        //验证必须以/**结束
        switch (builder.charAt(builder.length() - 1)) {
            case '/':
                builder.append("**");
                break;
            case '*':
                if (builder.length() > 1 && builder.charAt(builder.length() - 2) != '*') {
                    builder.append('*');
                }
                break;
            default:
                builder.append("/**");
                break;
        }
        return builder.toString();
    }
}
