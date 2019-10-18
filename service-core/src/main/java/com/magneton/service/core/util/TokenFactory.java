package com.magneton.service.core.util;

import com.alibaba.fastjson.JSON;
import com.google.common.hash.Hashing;

/**
 * 提供Token信息服务
 *
 * @author zhangmingshuang
 * @since 2019/4/27
 */
public final class TokenFactory {

    public static TokenFactory create(String password) {
        return new TokenFactory(password);
    }

    private final String TOKEN_PASSWORD;
    private final String IV;

    private TokenFactory(String password) {
        TOKEN_PASSWORD = Hashing.sha256().hashBytes(password.getBytes()).toString();
        IV = Hashing.hmacMd5(TOKEN_PASSWORD.getBytes()).toString();
    }

    public String createToken(Object msg) throws Throwable {
        return createToken(JSON.toJSONString(msg));
    }

    public String createToken(String msg) throws Throwable {
        return AESUtils.aes128CBCEncrypt(msg, TOKEN_PASSWORD, IV);
    }

    public String getInfo(String msg) throws Throwable {
        return AESUtils.aes128CBCDecrypt(msg, TOKEN_PASSWORD, IV);
    }
}
