package com.needayeah.elastic.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizRecord {

    private Long seq;
    private Long createTime;
    private Long recordTime;
    /** 操作发起者 */
    private String initiator;
    /** 记录主体 */
    private String subject;
    /** 记录主体标识 */
    private String subjectId;
    /** 记录操作者 */
    private String operator;
    /** 记录内容 */
    private String operation;

}
