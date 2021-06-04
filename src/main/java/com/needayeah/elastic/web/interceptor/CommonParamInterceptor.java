package com.needayeah.elastic.web.interceptor;


import com.needayeah.elastic.common.constant.PrivilegeConstant;
import com.needayeah.elastic.common.utils.HeaderThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * 设置公共参数
 */
@Aspect
@Component
@Slf4j
public class CommonParamInterceptor {


    @Around("execution(* com.needayeah.elastic.web.controller.*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object request = null;
        Object response = null;
        Object[] args = point.getArgs();
        if (Objects.nonNull(args) && args.length > 0) {
            for (Object obj : args) {
                if (!(obj instanceof HttpServletRequest || obj instanceof HttpServletResponse)) {
                    request = obj;
                    break;
                }
            }
        }
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String methodName = getCurrentMethodName(point);
        if (ObjectUtils.isEmpty(methodName)) {
            response = point.proceed();
            return response;
        }

        String requestURL = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        if (method.equals(HttpMethod.GET.name()) || PrivilegeConstant.specialMethodName.contains(methodName)) {
            response = point.proceed();
            return response;
        }

        String empId = "1234567";//HeaderThreadLocal.getThreadInstance().getEmployeeId();
        String name = "张三";//HeaderThreadLocal.getThreadInstance().getName();
        if (ObjectUtils.isEmpty(empId) || ObjectUtils.isEmpty(name)) {
            response = point.proceed();
            return response;
        }
        /**
         * 设置公用参数
         */

        setCommonField(request, empId, name);
        response = point.proceed();
        return response;

    }

    private void setCommonField(Object request, String empId, String name) {

        List<String> commonFieldIds = PrivilegeConstant.commonFieldIds;
        for (String str : commonFieldIds) {
            try {
                Field fieldStr = request.getClass().getDeclaredField(str);
                fieldStr.setAccessible(true);
                if (ObjectUtils.isEmpty(fieldStr.get(request))) {
                    fieldStr.set(request, empId);
                }
            } catch (Exception e) {

            }
        }
        List<String> commonFieldNames = PrivilegeConstant.commonFieldNames;
        for (String str : commonFieldNames) {
            try {
                Field fieldStr = request.getClass().getDeclaredField(str);
                fieldStr.setAccessible(true);
                fieldStr.set(request, name);
            } catch (Exception e) {

            }
        }

    }

    private String getCurrentMethodName(ProceedingJoinPoint point) {
        try {
            Signature sig = point.getSignature();
            MethodSignature msig = null;
            if (!(sig instanceof MethodSignature)) {
                return null;
            }
            msig = (MethodSignature) sig;
            Object target = point.getTarget();
            Method currentMethod = target.getClass().getDeclaredMethod(msig.getName(), msig.getParameterTypes());
            return currentMethod.getName();
        } catch (NoSuchMethodException e) {
            log.error("getCurrentMethodName error {}", e);
        }
        return null;
    }

}
