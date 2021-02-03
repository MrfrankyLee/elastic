package com.needayeah.elastic.common;

import com.google.common.collect.Lists;
import com.needayeah.elastic.common.utils.BeanUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
public class Page<T> implements Serializable {

    public static <T> Page<T> EMPTY_PAGE() {
        return new Page<>(0, Lists.newArrayList());
    }

    public static <T> Page<T> of(long total, List<T> datas) {
        return new Page<>(total, datas);
    }

    private long total;

    private List<T> datas;

    public boolean notEmpty() {
        return !CollectionUtils.isEmpty(this.datas);
    }

    public Page(long total, List<T> datas) {
        this.total = total;
        this.datas = datas;
    }

    public Page(List<T> datas) {
        this.total = 0;
        this.datas = datas;
    }

    public void ifPresent(Consumer<List<T>> listConsumer) {
        if (this.getDatas() != null && this.getDatas().size() > 0) {
            listConsumer.accept(this.datas);
        }
    }


    public <S> Page<S> transform(Class<S> clazz) {
        List<S> sList = BeanUtils.batchTransform(clazz, datas);
        return Page.of(total, sList);
    }

}
