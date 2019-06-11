package com.magneton.service.core.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP工具类
 *
 * @author zhangmsh
 */
public final class IpUtil {
    /**
     * ip范围
     */
    private static final int[][] RANGE = {
            //36.56.0.0-36.63.255.255
            {607649792, 608174079},
            //61.232.0.0-61.237.255.255
            {1038614528, 1039007743},
            //106.80.0.0-106.95.255.255
            {1783627776, 1784676351},
            //121.76.0.0-121.77.255.255
            {2035023872, 2035154943},
            //123.232.0.0-123.235.255.255
            {2078801920, 2079064063},
            //139.196.0.0-139.215.255.255
            {-1950089216, -1948778497},
            //171.8.0.0-171.15.255.255
            {-1425539072, -1425014785},
            //182.80.0.0-182.92.255.255
            {-1236271104, -1235419137},
            //210.25.0.0-210.47.255.255
            {-770113536, -768606209},
            //222.16.0.0-222.95.255.255
            {-569376768, -564133889},
    };
    public static final String[] IP_HEADERS = {
            "X-Real-IP",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
    };

    /**
     * 取得随机IP地址
     *
     * @return IP串
     */
    public static String getRandomIp() {
        Random random = new Random();
        int index = random.nextInt(10);
        int range = RANGE[index][0];
        long ip = random.nextInt(RANGE[index][1] - range);
        return num2ip(ip);
    }

    public static String ipStandard(String ips, String defaultStr) {
        if (StringUtil.isEmpty(ips) || ips.indexOf(",") == -1) {
            return defaultStr;
        }
        String[] ipArray = ips.split(",");
        StringBuilder builder = new StringBuilder();
        for (String ip : ipArray) {
            if (!isIP(ip)) {
                continue;
            }
            builder.append(ip);
        }
        return builder.toString();
    }


    public static long parseLong(String ipAddr) {
        boolean notIP = ipAddr == null || ipAddr.indexOf(".") == -1;
        if (notIP) {
            return 0L;
        }
        try {
            long ip = 0;
            String[] vals = ipAddr.split("\\.");
            for (int i = 0, l = vals.length; i < l; i++) {
                ip += Long.parseLong(vals[i]) << (24 - (i << 3));
            }
            return ip;
        } catch (Exception e) {
            return 0L;
        }
    }

    public static long parseIp(HttpServletRequest request) {
        return parseLong(getIp(request));
    }

    public static String getIp(HttpServletRequest request) {
        String ip;
        for (int i = 0, l = IP_HEADERS.length; i < l; i++) {
            ip = request.getHeader(IP_HEADERS[i]);
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                }
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static long getLongIP(HttpServletRequest request) {
        String ip = getIp(request);
        long lip = parseLong(ip);
        if (lip < 0) {
            lip = parseLong(request.getRemoteAddr());
        }
        return lip;
    }

    public static boolean isIpAllow(HttpServletRequest request, String allowIp) {
        if ("*".equals(allowIp)) {
            return true;
        }
        String ip = IpUtil.getIp(request);
        if (!allowIp.equals(ip)) {
            if (allowIp.indexOf(",") > 0) {
                String[] ips = ip.split(",");
                for (String i : ips) {
                    if (ip.equals(allowIp)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return true;
    }

    /**
     * 判断IP格式和范围
     *
     * @param addr IP串
     * @return 是否是IP
     */
    public static boolean isIP(String addr) {
        if (addr == null || "".equals(addr) || addr.length() < 7 || addr.length() > 15) {
            return false;
        }
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

    /**
     * 将十进制转换成ip地址
     *
     * @param ip 整数值IP地址
     * @return IP串
     */
    public static String num2ip(long ip) {
        StringBuilder ipAddress = new StringBuilder(18);
        return ipAddress.append((int) ((ip >> 24) & 0xff))
                .append(".")
                .append((int) ((ip >> 16) & 0xff))
                .append(".")
                .append((int) ((ip >> 8) & 0xff))
                .append(".")
                .append((int) (ip & 0xff)).toString();
    }


}
