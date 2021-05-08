package com.needayeah.elastic.config.oss;


/**
 * @author lixiaole
 */
public interface OssConstant {


    /**
     * 文件过期时间(月)
     */
    int FILE_EXPIRES_COUNT_IN_MONTHS = 12 * 5;

    /**
     * 上传失败提示语
     */
    String OSS_UPLOAD_ERROR_MSG = "上传失败，请重试！";

    /**
     * 下载失败提示语
     */
    String OSS_DOWNLOAD_ERROR_MSG = "下载失败，请重试！";

}
