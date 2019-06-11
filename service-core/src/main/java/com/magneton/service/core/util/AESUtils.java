package com.magneton.service.core.util;

import com.magneton.service.core.io.Bytes;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES加解密工具类
 *
 * @author zhangmsh
 * @since 2019-04-27
 */
public class AESUtils {

    /**
     * AES的Key合法大小
     */
    private static final int[] AES_KEYSIZES = new int[]{16};

    /**
     * 算法/模式/填充
     */
    private static final String CIPHERMODE = "AES/CBC/PKCS5Padding";

    public static byte[] convertToEnsureSize(String key) {
        int len = key.length();
        int legal = 0;
        int max = AES_KEYSIZES[AES_KEYSIZES.length - 1];
        for (int size : AES_KEYSIZES) {
            if (len <= size) {
                legal = size;
                break;
            }
            if (size >= max) {
                legal = max;
                break;
            }
        }
        byte[] bytes = key.getBytes();
        if (len != legal) {
            bytes = Arrays.copyOf(bytes, legal);
            //数据填充
            for (int i = len; i < legal; ++i) {
                bytes[i] = 'x';
            }
        }
        return bytes;
    }


    /**
     * 创建密钥
     *
     * @param key 加密Key
     * @return SecretKeySpec
     */
    private static SecretKeySpec createSecretKeySpec(String key) {
        return new SecretKeySpec(convertToEnsureSize(key), "AES");
    }

    /**
     * 创建初始化向量
     *
     * @param iv 初始化向量
     * @return IvParameterSpec
     */
    private static IvParameterSpec createIvParameterSpec(String iv) {
        return new IvParameterSpec(convertToEnsureSize(iv));
    }

    /**
     * AES-128/CBC算法加密字节数据
     *
     * @param content
     * @param password 密钥
     * @param iv       初始化向量
     * @return byte[]
     * @throws Throwable
     */
    public static String aes128CBCEncrypt(String content, String password, String iv)
            throws Throwable {
        Cipher cipher = Cipher.getInstance(CIPHERMODE);
        cipher.init(Cipher.ENCRYPT_MODE, createSecretKeySpec(password), createIvParameterSpec(iv));
        byte[] result = cipher.doFinal(content.getBytes());
        return Bytes.bytes2hex(result);
    }

    /**
     * AES-128/CBC算法解密字节数组
     *
     * @param content
     * @param password 密钥
     * @param iv       初始化向量
     * @return byte[] 解密后的数组
     * @throws Throwable
     */
    public static String aes128CBCDecrypt(String content, String password, String iv)
            throws Throwable {
        Cipher cipher = Cipher.getInstance(CIPHERMODE);
        cipher.init(Cipher.DECRYPT_MODE, createSecretKeySpec(password), createIvParameterSpec(iv));
        byte[] bytes = Bytes.hex2bytes(content);
        byte[] result = cipher.doFinal(bytes);
        return new String(result);
    }


    /**
     * AES-128 CBC加密方式， 加密后使用Base64转码
     *
     * @param content 待加密内容
     * @param key     密钥
     * @param iv      初始化向量
     * @return String
     * @throws Throwable
     */
    public static String aesCBCEncrypt(String content, String key, String iv)
            throws Throwable {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, createSecretKeySpec(key), createIvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return Bytes.bytes2hex(encrypted);
    }

    /**
     * AES-128 CBC解密方式
     *
     * @param content 待解密的Base64字符串
     * @param key     密钥
     * @param iv      初始化向量
     * @return String
     */
    public static String aesCBCDecrypt(String content, String key, String iv)
            throws Throwable {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, createSecretKeySpec(key), createIvParameterSpec(iv));
        byte[] bytes = Bytes.hex2bytes(content);
        byte[] original = cipher.doFinal(bytes);
        return new String(original);
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param key     密钥
     * @return String
     * @throws Throwable
     */
    public static String encrypt(String content, String key)
            throws Throwable {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(key.getBytes()));
        byte[] bytes = kgen.generateKey().getEncoded();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(bytes, "AES"));
        byte[] result = cipher.doFinal(content.getBytes());
        return Bytes.bytes2hex(result);
    }

    /**
     * 解密
     *
     * @param content 待加密内容
     * @param key     密钥
     * @return String
     * @throws Throwable
     */
    public static String decrypt(String content, String key)
            throws Throwable {
        byte[] bytes = Bytes.hex2bytes(content);
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(key.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] result = cipher.doFinal(bytes);
        return new String(result);
    }

}