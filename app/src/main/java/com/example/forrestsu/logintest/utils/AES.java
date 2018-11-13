package com.example.forrestsu.logintest.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.xml.transform.OutputKeys.ENCODING;

/**
 * AES 算法
 * <p/>
 * 算法采用加密模式：CBC；数据块：128；填充：PKCS5Padding
 * <p/>
 * key 与向量字符串的长度为 16 位
 *
 * @author Deniro Li (lisq037@163.com)
 *         2018/3/17
 */
public class AES {

    /**
     * 算法名称
     */
    public static final String NAME = "AES";

    /**
     * 加密模式：CBC；数据块：128；填充：PKCS5Padding
     */
    public final String MODE = "AES/CBC/PKCS5Padding";

    /**
     * KEY 与 向量字符串的长度
     */
    public static final int LENGTH = 16;


    /**
     * 加密用的 KEY
     */
    private String key;

    /**
     * 向量，用于增加加密强度
     */
    private String ivParameter;

    /**
     * @param key         加密用的 KEY
     * @param ivParameter 偏移量
     */
    public AES(String key, String ivParameter) {
        this.key = key;
        this.ivParameter = ivParameter;
    }


    /**
     * 加密
     *
     * @param s 要加密的字符串
     * @return 加密后的字符串
     */
    public String encode(String s)  throws AesException {
        String result;
        try {

            Cipher cipher = Cipher.getInstance(MODE);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), NAME), iv);
            byte[] bytes = cipher.doFinal(s.getBytes(ENCODING));
            result = new BASE64Encoder().encode(bytes);
        } catch (Exception e) {
            throw new AesException("加密", e);
        }
        return result;
    }

    /**
     * 解密
     *
     * @param s 待解密的字符串
     * @return 解密后的字符串
     */
    public String decode(String s)  throws AesException {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), NAME);
            Cipher cipher = Cipher.getInstance(MODE);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(s)), ENCODING);
        } catch (Exception e) {
            throw new AesException("解密", e);
        }
    }
}