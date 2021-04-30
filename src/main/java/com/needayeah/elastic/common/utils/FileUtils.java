package com.needayeah.elastic.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author lixiaole
 * @date 2021/4/28
 */
public class FileUtils {

    /**
     * 读取文件
     *
     * @param filePath
     * @return
     */
    public static String readContent(String filePath) {
        try {
            InputStream is = FileUtils.class.getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
