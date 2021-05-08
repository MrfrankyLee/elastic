package com.needayeah.elastic.web.interceptor;


import com.needayeah.elastic.common.annotation.DataPrivilegeInjection;
import com.needayeah.elastic.common.annotation.PrivilegeFieldEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author: lixiaole
 * @Date: 2021/4/23 09:13
 * @Description:
 */
@Component
@Aspect
@Slf4j
public class DataPrivilegeAspect {


    @Around("@annotation(filter)")
    public Object around(ProceedingJoinPoint point, DataPrivilegeInjection filter) throws Throwable {
        /* 获取token
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        */

        PrivilegeFieldEnum[] fields = filter.fields();
        //权限控制
        setDataPrivilegeFields(point.getArgs(), filter.fieldName(), fields);
        return point.proceed();
    }


    private void setDataPrivilegeFields(Object[] args, String actualFiledName, PrivilegeFieldEnum[] fields) {
        Object request = null;
        if (Objects.nonNull(args)) {
            for (Object obj : args) {
                if (!(obj instanceof HttpServletRequest || obj instanceof HttpServletResponse)) {
                    request = obj;
                    break;
                }
            }
        }
        if (Objects.isNull(request)) {
            return;
        }

        //导出接口的查询条件参数在里层
        if (StringUtils.hasLength(actualFiledName)) {
            try {
                Field filed = request.getClass().getDeclaredField(actualFiledName);
                filed.setAccessible(true);
                request = filed.get(request);
            } catch (Exception e) {

            }
        }

        //查询用户数据权限
        for (PrivilegeFieldEnum fieldEnum : fields) {
            try {
                Field filed = request.getClass().getDeclaredField(fieldEnum.getFieldName());
                filed.setAccessible(true);
                //判断如果已经有值则不注入
                if (filed.getGenericType().toString().equals("class java.lang.String")) {
                    String param = (String) filed.get(request);
                    if (Strings.isNotEmpty(param)) {
                        break;
                    } else {
                        param = "手机";
                    }
                    filed.set(request, param);
                }
            } catch (Exception e) {

            }
        }

    }
}
