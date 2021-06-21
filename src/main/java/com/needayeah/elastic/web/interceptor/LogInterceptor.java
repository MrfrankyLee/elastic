package com.needayeah.elastic.web.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixiaole
 * @date 2021/6/21
 */
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    @Pointcut("execution(* com.needayeah.elastic.web.controller.*.*(..)))")
    private void excludeController() {

    }

    @Around("excludeController()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object request = null;
        Object response = null;
        Object[] args = point.getArgs();
        for (Object obj : args) {
            if (!(obj instanceof HttpServletRequest || obj instanceof HttpServletResponse)) {
                request = obj;
                break;
            }
        }
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String method = httpRequest.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            response = point.proceed();
            return response;
        }
        String requestURL = httpRequest.getRequestURI();
        response = point.proceed();
        log.info(JSON.toJSONString(requestURL));
        log.info(JSON.toJSONString(request));
        log.info(JSON.toJSONString(response));
        return response;
    }
}
