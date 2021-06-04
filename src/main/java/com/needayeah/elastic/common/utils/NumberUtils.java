package com.needayeah.elastic.common.utils;

/**
 * 数字工具类
 *
 * @author lixiaole
 * @date 2021/6/2
 */
public class NumberUtils {

    /**
     * 生成前缀+给定长度的字符串
     *
     * @param prefix       前缀
     * @param number       数字
     * @param expectLength 长度
     * @return String
     */
    public static String formatLength(String prefix, Long number, int expectLength) {
        return prefix + formatLength(number, expectLength);
    }

    /**
     * 生成给定长度的字符串
     *
     * @param number       数字
     * @param expectLength 长度
     * @return String
     */
    public static String formatLength(Object number, int expectLength) {
        if (!(number instanceof Number)) {
            return number.toString();
        } else if (number.toString().length() > expectLength) {
            return number.toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < expectLength - number.toString().length(); ++i) {
                stringBuilder.append("0");
            }

            return stringBuilder.append(number).toString();
        }
    }
}
