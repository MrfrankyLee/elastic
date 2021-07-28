package com.needayeah.elastic.common.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author lixiaole
 * @date 2021/7/28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /** 默认最大请求次数 10次 */
    long DEFAULT_REQUEST = 10;

    /** 默认值 */
    @AliasFor("max") long value() default DEFAULT_REQUEST;

    /** max 最大请求数 */
    @AliasFor("value") long max() default DEFAULT_REQUEST;

    /** 限流key */
    String key() default "";

    /** 默认时长 1分钟 */
    long timeOut() default 1;

    /** 超时时间单位，默认 分钟 */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
