package com.needayeah.elastic.config.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.needayeah.elastic.common.utils.HttpClientUtil;
import com.needayeah.elastic.common.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 阿里云OSS存储服务
 *
 * @author lixiaole
 */
@Slf4j
@Component
public class OssUtil {

    /**
     * 所在地域对应的Endpoint 以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
     */
    @Value("${aliYun.oss.endpoint:}")
    private String endpoint;

    /**
     * 阿里云账号AccessKey
     */
    @Value("${aliYun.oss.accessKeyId:}")
    private String accessKeyId;

    /**
     * 阿里云账号accessKeySecret
     */
    @Value("${aliYun.oss.accessKeySecret:}")
    private String accessKeySecret;

    /**
     * Bucket名称
     */
    @Value("${aliYun.oss.bucketName:}")
    private String bucketName;

    /**
     * 上传后的文件路径
     */
    @Value("${aliYun.oss.filePath:}")
    private String filePath;

    public OSS client;

    @Bean
    public OSS client() {
        if (Objects.isNull(client)) {
            client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        return client;
    }


    /**
     * 上传
     *
     * @param originalFileName
     * @param fileStream
     * @return
     */
    public String uploadFile(String originalFileName, InputStream fileStream) {
        try {
            int extensionStartIndex = originalFileName.lastIndexOf('.');
            String fileExtension = extensionStartIndex == -1 ? "" : originalFileName.substring(extensionStartIndex);
            String key = filePath + SnowFlake.strNextId() + fileExtension;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, fileStream);
            PutObjectResult putObjectResult = client.putObject(putObjectRequest);
            log.info("upload file response result :" + JSON.toJSONString(putObjectRequest));
            return client.generatePresignedUrl(bucketName, key, DateUtil.offsetMonth(new Date(), OssConstant.FILE_EXPIRES_COUNT_IN_MONTHS)).toString();
        } catch (Exception e) {
            log.error("Upload file to OSS occurred an error: {}.", e);
            return OssConstant.OSS_UPLOAD_ERROR_MSG;
        }
    }


    /**
     * 按照String 上传
     *
     * @param urlStr
     * @param content
     * @return
     */
    public String getFromUrlAndUpload(String urlStr, String content) {
        try {
            URL url = new URL(urlStr);
            String filename = url.getPath().replaceFirst("^/", "");
            OSSObject ossObject = client.getObject(bucketName, filename);
            if (ossObject == null) {
                log.error("OSSUtil getFromUrlAndUpload ossObject null");
                return OssConstant.OSS_UPLOAD_ERROR_MSG;
            }
            Map<String, String> userMetadata = ossObject.getObjectMetadata().getUserMetadata();
            String originalFileName = userMetadata.get("originalfilename") == null ? "unknown" : userMetadata.get("originalfilename");
            InputStream fileInputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            return uploadFile(originalFileName, fileInputStream);
        } catch (MalformedURLException e) {
            log.error("Upload file to OSS occurred an error: {}.", e);
            return OssConstant.OSS_UPLOAD_ERROR_MSG;
        }

    }

    /**
     * 下载文件
     *
     * @param downloadUrl
     * @return
     */
    public String downloadAsString(String downloadUrl) {
        try {
            if (StrUtil.isBlank(downloadUrl)) {
                throw new RuntimeException("Download url wrong: " + downloadUrl);
            }
            return HttpClientUtil.sendGetRequest(downloadUrl);
        } catch (Exception e) {
            log.error("Download file from OSS occurred an error: {}.", e);
            return OssConstant.OSS_DOWNLOAD_ERROR_MSG;
        }
    }
}
