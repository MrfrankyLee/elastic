package com.needayeah.elastic.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.needayeah.elastic.common.annotation.RateLimiter;
import com.xxl.job.core.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;

/**
 * @author lixiaole
 * @date 2021/7/28
 */
@Slf4j
@Aspect
@Component
public class RateLimit {

    private final static String REDIS_LIMIT_KEY_PREFIX = "limit:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private RedisScript<Long> limitRedisScript;

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint point, RateLimiter rateLimiter) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String key = rateLimiter.key();
        // 默认用类名+方法名做限流的 key 前缀
        if (StrUtil.isBlank(key)) {
            key = method.getDeclaringClass().getName() + StrUtil.DOT + method.getName();
        }
        // 最终限流的 key 为 前缀 + IP地址
        // TODO: 此时需要考虑局域网多用户访问的情况，因此 key 后续需要加上方法参数更加合理
        key = key + StrUtil.C_COLON + IpUtil.getIp();
        long max = rateLimiter.max();
        long timeout = rateLimiter.timeUnit().toMillis(rateLimiter.timeOut());
        boolean limited = shouldLimited(key, max, timeout);
        if (limited) {
            throw new RuntimeException("手速太快了，慢点儿吧~");
        }
        return point.proceed();
    }

    private boolean shouldLimited(String key, long max, long timeout) {
        // 最终的 key 格式为：
        // limit:自定义key:IP
        // limit:类名.方法名:IP
        key = REDIS_LIMIT_KEY_PREFIX + key;
        // 当前时间毫秒数
        long now = Instant.now().toEpochMilli();
        long expired = now - timeout;
        // 注意这里必须转为 String,否则会报错 java.lang.Long cannot be cast to java.lang.String
        Long executeTimes = redisTemplate.execute(limitRedisScript, Collections.singletonList(key), now + "", timeout + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("【{}】在单位时间 {} 毫秒内已达到访问上限，当前接口上限 {}", key, timeout, max);
                return true;
            } else {
                log.info("【{}】在单位时间 {} 毫秒内访问 {} 次", key, timeout, executeTimes);
                return false;
            }
        }
        return false;
    }
}
