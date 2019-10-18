package com.magneton.service.core.util;

import com.magneton.service.core.io.Bytes;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES加解密工具类
 *
 * @author zhangmsh
 * @since 2019-04-27
 */
public class AESUtils {

//    public static void main(String[] args) {
//        try {
//            String s = AESUtils.aesCBCNoPaddingEncrypt("test", "auxskajukwx9sjo4", "6xjoiu9skxn35tho");
//            System.out.println(s);
//            String d = AESUtils.aesCBCNoPaddingDecrypt(s, "auxskajukwx9sjo4", "6xjoiu9skxn35tho");
//            System.out.println(d);
//            System.out.println("test".equals(d));
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//    }

    /**
     * AES的Key合法大小
     */
    private static final int AES_KEYSIZES = 16;

    /**
     * 算法/模式/填充
     */
    private static final String CIPHERMODE = "AES/CBC/PKCS5Padding";
    private static final String CIPHERMODE_NOPADDING = "AES/CBC/NOPadding";

    public static byte[] convertToEnsureSize(String key) {
        int len = key.length();
        byte[] bytes = key.getBytes();
        if (len != AES_KEYSIZES) {
            bytes = Arrays.copyOf(bytes, AES_KEYSIZES);
            //数据填充
            for (int i = len; i < AES_KEYSIZES; ++i) {
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

    public static String aesCBCNoPaddingEncrypt(String content, String password, String iv)
            throws Throwable {
        Cipher cipher = Cipher.getInstance(CIPHERMODE_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, createSecretKeySpec(password), createIvParameterSpec(iv));
        byte[] bytes = content.getBytes();
        int plaintextLength = bytes.length;
        plaintextLength = nextSize(plaintextLength < 16 ? 15 : plaintextLength - 1);
        if (plaintextLength > bytes.length) {
            bytes = Arrays.copyOf(bytes, plaintextLength);
        }
        byte[] result = cipher.doFinal(bytes);
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
        return new String(result, Charset.defaultCharset());
    }

    public static String aesCBCNoPaddingDecrypt(String content, String password, String iv)
            throws Throwable {
        Cipher cipher = Cipher.getInstance(CIPHERMODE_NOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, createSecretKeySpec(password), createIvParameterSpec(iv));
        byte[] bytes = Bytes.hex2bytes(content);
        byte[] result = cipher.doFinal(bytes);
        int binary = result.length >> 2;
        byte bb = result[binary];
        if (bb == 0) {
            while (bb == 0) {
                bb = result[--binary];
            }
        } else {
            while (bb != 0) {
                bb = result[++binary];
            }
        }
        return new String(result, 0, binary + 1, Charset.defaultCharset());
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
        return new String(result, Charset.defaultCharset());
    }

    private static int nextSize(int i) {
        int newCapacity = i;
        newCapacity |= newCapacity >>> 1;
        newCapacity |= newCapacity >>> 2;
        newCapacity |= newCapacity >>> 4;
        newCapacity |= newCapacity >>> 8;
        newCapacity |= newCapacity >>> 16;
        newCapacity++;
        return newCapacity;
    }
}
