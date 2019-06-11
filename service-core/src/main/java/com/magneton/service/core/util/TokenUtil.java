package com.magneton.service.core.util;

import com.alibaba.fastjson.JSON;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 提供Token信息服务
 *
 * @author zhangmingshuang
 * @since 2019/4/27
 */
public final class TokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtil.class);

    private static String TOKEN_PASSWORD;
    private static String IV;

    static {
        try (InputStream is = TokenUtil.class.getResourceAsStream("token.sec");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            //计算Hash
            TOKEN_PASSWORD = Hashing.sha256().hashBytes(builder.toString().getBytes()).toString();
            IV = Hashing.hmacMd5(TOKEN_PASSWORD.getBytes()).toString();
        } catch (Throwable e) {
            LOGGER.warn("file token.sec missing. use default password and IvParameter");
            TOKEN_PASSWORD = "zlkvjal;skdfjpqewuirsjdvakldsjhflwejrq;asdfkjsadlfkje";
            IV = "askvjp8uewrq3j4afdszcva";
        }
    }

    public static String createToken(Object msg) throws Throwable {
        return createToken(JSON.toJSONString(msg));
    }

    public static String createToken(String msg) throws Throwable {
        return AESUtils.aes128CBCEncrypt(msg, TOKEN_PASSWORD, IV);
    }

    public static String getInfo(String msg) throws Throwable {
        return AESUtils.aes128CBCDecrypt(msg, TOKEN_PASSWORD, IV);
    }
}
