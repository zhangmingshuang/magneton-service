package com.magneton.service.core.util;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhangmingshuang
 * @since 2019/4/26
 */
public final class PrintWriteUtil {

    private PrintWriteUtil() {

    }

    public static void write(HttpServletResponse response,
                             String contentType,
                             String content,
                             Charset charset) {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        response.setContentType(contentType);
        response.setCharacterEncoding(charset.name());
        try (PrintWriter pw = response.getWriter()) {
            pw.write(content);
            pw.flush();
        } catch (IOException e) {

        }
    }

    public static void writeMultipartFormData(HttpServletResponse response, String content) {
        write(response, "multipart/form-data", content);
    }

    public static void write(HttpServletResponse response, String contentType, String content) {
        write(response, contentType, content, null);
    }

    public static void writeXml(HttpServletResponse response, String content) {
        write(response, "text/xml", content, null);
    }

    public static void writeHtml(HttpServletResponse response, String content) {
        write(response, "text/html", content, null);
    }

    public static void writeJSON(HttpServletResponse response, Object object) {
        write(response, "application/json;charset=utf-8", JSON.toJSONString(object), null);
    }
}
