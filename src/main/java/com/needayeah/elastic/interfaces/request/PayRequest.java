package com.needayeah.elastic.interfaces.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lixiaole
 * @date 2021/5/18
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PayRequest implements Serializable {
    private static final long serialVersionUID = -2939401547315763840L;

    private String orderId;

    private String paymentType;

}
