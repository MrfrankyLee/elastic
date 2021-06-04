package com.needayeah.elastic.web.interceptor;


import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.common.utils.SendResponseUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ErrorPageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (request.getRequestURI().endsWith("/error")) {
            SendResponseUtil.sendResponse(response, new Result(5000, "系统繁忙,请稍后重试"));
            return false;
        }
        return true;

    }

}
