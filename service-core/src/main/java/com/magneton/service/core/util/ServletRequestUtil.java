package com.magneton.service.core.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author zhangmingshuang
 * @since 2019/6/12
 */
public class ServletRequestUtil {

    public static void main(String[] args) {
        JSONObject obj = new JSONObject();

    }

    public static Integer getInteger(ServletRequest request, String name) {
        return ServletRequestUtil.getInteger(request, name, null);
    }

    public static Integer getInteger(ServletRequest request, String name, Integer defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public static int getIntValue(ServletRequest request, String name) {
        return ServletRequestUtil.getIntValue(request, name, 0);
    }

    public static int getIntValue(ServletRequest request, String name, int defaultValue) {
        Integer value = ServletRequestUtil.getInteger(request, name, null);
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    public static Long getLong(ServletRequest request, String name, Long defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Long.parseLong(value);
    }

    public static Long getLong(ServletRequest request, String name) {
        return ServletRequestUtil.getLong(request, name, null);
    }

    public static long getLongValue(ServletRequest request, String name) {
        return ServletRequestUtil.getLongValue(request, name, 0L);
    }

    public static long getLongValue(ServletRequest request, String name, long defaultValue) {
        Long value = ServletRequestUtil.getLong(request, name, null);
        if (value == null) {
            return defaultValue;
        }
        return value.longValue();
    }

    public static Float getFloat(ServletRequest request, String name, Float defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }

    public static Float getFloat(ServletRequest request, String name) {
        return ServletRequestUtil.getFloat(request, name, null);
    }

    public static float getFloatValue(ServletRequest request, String name) {
        return ServletRequestUtil.getFloatValue(request, name, 0F);
    }

    public static float getFloatValue(ServletRequest request, String name, float defaultValue) {
        Float value = ServletRequestUtil.getFloat(request, name, null);
        if (value == null) {
            return defaultValue;
        }
        return value.floatValue();
    }

    public static Double getDouble(ServletRequest request, String name, Double defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    public static Double getDouble(ServletRequest request, String name) {
        return ServletRequestUtil.getDouble(request, name, null);
    }

    public static double getDoubleValue(ServletRequest request, String name) {
        return ServletRequestUtil.getDoubleValue(request, name, 0D);
    }

    public static double getDoubleValue(ServletRequest request, String name, double defaultValue) {
        Double value = ServletRequestUtil.getDouble(request, name, null);
        if (value == null) {
            return defaultValue;
        }
        return value.doubleValue();
    }

    public static BigDecimal getBigDecimal(ServletRequest request, String name, BigDecimal defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return new BigDecimal(value);
    }

    public static BigDecimal getBigDecimal(ServletRequest request, String name) {
        return ServletRequestUtil.getBigDecimal(request, name, null);
    }

    public static BigInteger getBigInteger(ServletRequest request, String name, BigInteger defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return new BigInteger(value);
    }

    public static BigInteger getBigInteger(ServletRequest request, String name) {
        return ServletRequestUtil.getBigInteger(request, name, null);
    }

    public static String getString(ServletRequest request, String name) {
        return ServletRequestUtil.getString(request, name, null);
    }

    public static String getString(ServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Boolean getBoolean(ServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null) {
            return Boolean.FALSE;
        }
        if (value.equalsIgnoreCase("true")
                || "1".equals(value)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean getBooleanValue(ServletRequest request, String name) {
        return ServletRequestUtil.getBoolean(request, name).booleanValue();
    }

    public static Byte getByte(ServletRequest request, String name, Byte defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Byte.parseByte(value);
    }

    public static Byte getByte(ServletRequest request, String name) {
        return ServletRequestUtil.getByte(request, name, null);
    }

    public static byte getByteValue(ServletRequest request, String name) {
        return ServletRequestUtil.getByteValue(request, name, (byte) 0);
    }

    public static byte getByteValue(ServletRequest request, String name, byte defaultValue) {
        Byte value = ServletRequestUtil.getByte(request, name, null);
        if (value == null) {
            return defaultValue;
        }
        return value.byteValue();
    }

    public static Short getShort(ServletRequest request, String name, Short defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return Short.parseShort(value);
    }

    public static Short getShort(ServletRequest request, String name) {
        return ServletRequestUtil.getShort(request, name, null);
    }

    public static short getShortValue(ServletRequest request, String name) {
        return ServletRequestUtil.getShortValue(request, name, (short) 0);
    }

    public static short getShortValue(ServletRequest request, String name, short defaultValue) {
        Short value = ServletRequestUtil.getShort(request, name, null);
        if (value == null) {
            return defaultValue;
        }
        return value.shortValue();
    }
}
