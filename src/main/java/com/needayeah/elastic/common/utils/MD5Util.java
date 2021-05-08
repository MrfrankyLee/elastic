package com.needayeah.elastic.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;


public class MD5Util {

    private static final Logger log = LoggerFactory.getLogger(MD5Util.class);

    private static char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMd5Sum(String inputStr) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] inputStrByte = inputStr.getBytes();
        digest.update(inputStrByte, 0, inputStrByte.length);


        byte[] md5sum = digest.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            char[] ob = new char[2];
            ob[0] = Digit[md5sum[i] >> 4 & 0x0F];
            ob[1] = Digit[md5sum[i] & 0x0F];
            String s = new String(ob);
            sb.append(s);
        }

        return sb.toString();
        //	BigInteger bigInt = new BigInteger(1, md5sum);
        //	String output = bigInt.toString(16);
        //	return output;
    }


    /**
     * 对字符串进行 MD5 加密
     *
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            log.error("", e);
            throw new RuntimeException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        byte[] encodedValue = md5.digest();
        int j = encodedValue.length;
        char finalValue[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte encoded = encodedValue[i];
            finalValue[k++] = Digit[encoded >> 4 & 0xf];
            finalValue[k++] = Digit[encoded & 0xf];
        }

        return new String(finalValue);
    }

    /**
     * 签名字符串
     *
     * @param text 需要签名的字符串
     * @param sign 签名结果
     * @return 签名结果
     */
    public static boolean verify(String text, String sign) {
        String mysign = md5(text);
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对文件进行 MD5 加密
     *
     * @param file 待加密的文件
     * @return 文件加密后的 MD5 值
     * @throws IOException
     */
    public static String md5(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            int n = 0;
            byte[] buffer = new byte[1024];
            do {
                n = is.read(buffer);
                if (n > 0) {
                    md5.update(buffer, 0, n);
                }
            } while (n != -1);
            is.skip(0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            is.close();
        }

        byte[] encodedValue = md5.digest();

        int j = encodedValue.length;
        char finalValue[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte encoded = encodedValue[i];
            finalValue[k++] = Digit[encoded >> 4 & 0xf];
            finalValue[k++] = Digit[encoded & 0xf];
        }

        return new String(finalValue);
    }

    public static String md5(String str, String key) {
        String oldStr = str + key;
//        log.info("加密前="+oldStr);
        String encryptedStr = MD5Util.md5(oldStr);
//        log.info("加密后="+encryptedStr);
        return encryptedStr;
    }

    public static String md5(TreeMap<String, String> params, String key) {
        StringBuilder md5Str = new StringBuilder();
        try {
            for (String k : params.keySet()) {
                md5Str.append(k).append("=").append(URLEncoder.encode(params.get(k), "UTF-8")).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("MD5加密失败, URLEncode编码不支持!");
            return "";
        }
        String oldStr = md5Str.toString();
        oldStr = oldStr.substring(0, oldStr.length() - 1) + key;
        log.info("加密前: " + oldStr);
        String encryptedStr = MD5Util.md5(oldStr);
        log.info("加密后: " + encryptedStr);
        return encryptedStr;
    }

    public static String sha1(TreeMap<String, String> params, String key) {
        StringBuilder shaStr = new StringBuilder();
        for (String val : params.values()) {
            shaStr.append(val).append("|");
        }
        String oldStr = shaStr.append(key).toString();
        log.info("加密前=" + oldStr);
        String encryptedStr = DigestUtils.sha1Hex(oldStr);
        log.info("加密后=" + encryptedStr);
        return encryptedStr;
    }

    public static String md5ForOldPay(TreeMap<String, String> params, String key) {
        StringBuilder md5Str = new StringBuilder();
        for (String k : params.keySet()) {
            md5Str.append(params.get(k)).append("|");
        }
        md5Str.append(key);
//        log.info("加密前={}", md5Str.toString());
        String encryptedStr = MD5Util.md5(md5Str.toString());
//        log.info("加密后={}", encryptedStr);
        return encryptedStr;
    }

    /**
     * @param requestSign 请求签名串
     * @param merchantKey 商户秘钥
     * @return
     * @author guojialin
     * MD5签名和验签
     */
    public static String signByMD5(String requestSign, String merchantKey) {
        String reqHmac = "";
        try {
            reqHmac = DigestUtils.md5Hex(requestSign + merchantKey).toUpperCase();
        } catch (Exception e) {
        }

        return reqHmac;
    }

    public static String md5ForPay(TreeMap<String, String> params, String key) {
        StringBuilder md5Str = new StringBuilder(key);
        for (String k : params.keySet()) {
            md5Str.append("|").append(params.get(k));
        }
//        log.info("加密前={}", md5Str.toString());
        String encryptedStr = MD5Util.md5(md5Str.toString());
//        log.info("加密后={}", encryptedStr);
        return encryptedStr;
    }

    public static boolean verify(TreeMap<String, String> params, String md5, String key) {
        String encryptedStr = md5(params, key);
        return encryptedStr.equals(md5);
    }

    public static boolean verifyCaseIns(TreeMap<String, String> params, String md5, String key) {
        String encryptedStr = md5(params, key).toUpperCase();
        return encryptedStr.equals(md5);
    }

    public static boolean verify(String str, String md5, String key) {
        String encryptedStr = md5(str, key);
        return encryptedStr.equals(md5);
    }

    public static String encryptMD5(String inputText) {
        return encrypt(inputText, "MD5");
    }

    private static String encrypt(String inputText, String algorithmName) {
        if ((inputText == null) || ("".equals(inputText.trim()))) {
            return "";
        }
        if ((algorithmName == null) || ("".equals(algorithmName.trim())))
            algorithmName = "md5";
        try {
            MessageDigest m = MessageDigest.getInstance(algorithmName);
            m.update(inputText.getBytes("UTF-8"));
            byte[] s = m.digest();
            return hex(s);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    private static String hex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Integer.toHexString(arr[i] & 0xFF | 0x100).substring(1, 3));
        }

        return sb.toString();
    }
}
