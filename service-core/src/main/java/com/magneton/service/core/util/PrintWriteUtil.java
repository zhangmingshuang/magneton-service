package com.magneton.service.core.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhangmingshuang
 * @since 2019/4/26
 */
public class PrintWriteUtil {

    public static void writeJSON(HttpServletResponse response, Object object) {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter pw = response.getWriter()) {
            pw.write(JSON.toJSONString(object));
            pw.flush();
        } catch (IOException e) {

        }
    }
}
