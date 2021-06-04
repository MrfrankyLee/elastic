package com.needayeah.elastic.common.utils;

import cn.hutool.http.ContentType;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author lixiaole
 * @date 2021/6/2
 */
public class SendResponseUtil {
    public SendResponseUtil() {
    }

    public static void sendResponse(HttpServletResponse resp, Result result) {
        try {
            resp.setContentType(ContentType.JSON.toString());
            resp.setCharacterEncoding("UTF-8");
            ServletOutputStream outputStream = resp.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            String jsonString = JSON.toJSONString(result);
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException var5) {
        }

    }
}
