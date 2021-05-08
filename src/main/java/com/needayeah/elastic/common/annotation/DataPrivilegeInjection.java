package com.needayeah.elastic.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: lixiaole
 * @Date: 2021/4/23 09:07
 * @Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPrivilegeInjection {

    PrivilegeFieldEnum[] fields();

    String fieldName() default "";

}
