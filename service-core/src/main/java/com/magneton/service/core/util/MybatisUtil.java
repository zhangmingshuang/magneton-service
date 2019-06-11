package com.magneton.service.core.util;

import com.magneton.service.core.Default;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  
 *  Mybatis帮忙工具
 *
 * @author zhangmsh
 * @since 2019-04-27
 */
public class MybatisUtil {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");


    /**
     * ResultMap转化
     *
     * @param clazz 要转化的类
     * @return String ResultMap代码
     */
    public static String toResultMap(Class<?> clazz) {

        String simpleName = clazz.getSimpleName();
        String type = clazz.getName();
        StringBuilder builder = new StringBuilder();
        //组装成如： Abc = abcMap
        String id = builder.append(Character.toLowerCase(simpleName.charAt(0)))
                .append(simpleName.substring(1)).append("Map").toString();

        builder.setLength(0);

        builder.append("<resultMap id=\"").append(id).append("\" type=\"").append(type).append("\">");
        builder.append(Default.NEW_LINE);

        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            int modifier = f.getModifiers();
            if (Modifier.isStatic(modifier) && Modifier.isFinal(modifier)) {
                continue;
            }
            String property = f.getName();
            String javaType = f.getType().getName();
            if ("serialVersionUID".equals(property)) {
                continue;//忽略掉这个属性
            }
            builder.append("\t<result column=\"")
                    .append(property2Column(property))
                    .append("\" jdbcType=\"")
                    .append(javaType2jdbcType(javaType.toLowerCase()))
                    .append("\" property=\"")
                    .append(property)
                    .append("\" />")
                    .append(Default.NEW_LINE);
        }
        builder.append("</resultMap>");
        return builder.toString();
    }

    private static String property2Column(String property) {
        Matcher matcher = humpPattern.matcher(property);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String javaType2jdbcType(String javaType) {
        if (javaType.contains("string")) {
            return "VARCHAR";
        } else if (javaType.contains("boolean")) {
            return "BIT";
        } else if (javaType.contains("byte")) {
            return "TINYINT";
        } else if (javaType.contains("short")) {
            return "SMALLINT";
        } else if (javaType.contains("int")) {
            return "INTEGER";
        } else if (javaType.contains("long")) {
            return "BIGINT";
        } else if (javaType.contains("double")) {
            return "DOUBLE";
        } else if (javaType.contains("float")) {
            return "REAL";
        } else if (javaType.contains("date")) {
            return "DATE";
        } else if (javaType.contains("timestamp")) {
            return "TIMESTAMP";
        } else if (javaType.contains("time")) {
            return "TIME";
        } else if (javaType.contains("bigdecimal")) {
            return "DECIMAL";
        } else {
            return "未知类型";
        }
    }
}