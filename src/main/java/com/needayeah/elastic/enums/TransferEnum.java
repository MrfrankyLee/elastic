package com.needayeah.elastic.enums;

/**
 * @discription 枚举转换接口，如果在使用BeanUtils拷贝对象属性值时，需要支持枚举对象转Integer和String对象，
 * 或者Integer和String转枚举对象时需要实现该接口
 */

public interface TransferEnum<T> {

    /**
     * 获取枚举对象中value属性值
     *
     * @return
     */
    T getValue();
}
