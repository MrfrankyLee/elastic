package com.needayeah.elastic.interfaces.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PayResultResponse {
    /**
     * 支付结果
     */
    private String result;
}
