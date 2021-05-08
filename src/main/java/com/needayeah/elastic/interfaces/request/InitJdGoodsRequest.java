package com.needayeah.elastic.interfaces.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lixiaole
 * @date 2021/5/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitJdGoodsRequest implements Serializable {
    private static final long serialVersionUID = 2151029732890489715L;

    private String keyWord;
}
