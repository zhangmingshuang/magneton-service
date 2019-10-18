package com.magneton.service.core.util;

/**
 * @author zhangmingshuang
 * @since 2019/9/9
 */
public class PathUtil {
    public static String getPath(String http, String url) {
        if (StringUtil.isEmpty(url)) {
            return null;
        }
        if (url.startsWith("http")) {
            return url;
        }
        return http + url;
    }
}
