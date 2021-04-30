package com.needayeah.elastic.common.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @discription
 */
@Data
@NoArgsConstructor
public class All<T> implements Serializable {

    private List<T> datas;

    public All(List<T> datas) {
        this.datas = datas;
    }
}
