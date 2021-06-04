package com.needayeah.elastic.common.utils;

import lombok.Data;

/**
 * @author lixiaole
 * @date 2021/6/2
 */
@Data
public class HeaderThreadLocal {
    private static ThreadLocal<HeaderThreadLocal> threadLocal = new ThreadLocal();
    private String employeeId;
    private String name;
    private String token;
    private String reqIp;

    private HeaderThreadLocal() {
    }

    public static HeaderThreadLocal getThreadInstance() {
        HeaderThreadLocal headerThreadLocal = (HeaderThreadLocal) threadLocal.get();
        if (null == headerThreadLocal) {
            headerThreadLocal = new HeaderThreadLocal();
            threadLocal.set(headerThreadLocal);
        }

        return headerThreadLocal;
    }
}
