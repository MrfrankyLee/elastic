package com.needayeah.elastic.web.interceptor;

import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.common.utils.SendResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author lixiaole
 * @date 2021/6/2
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 访问白名单  无需登录
     */
    @Value("${spring.authWhiteList}")
    private String authWhiteList;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            return true;
        }
        // 查看是否白名单
        String requestURL = request.getRequestURI();
        if (Objects.nonNull(authWhiteList)) {
            String[] whiteListStrs = authWhiteList.split(",");
            if (Arrays.asList(whiteListStrs).stream().anyMatch(requestURL::endsWith)) {
                return true;
            }
        }

        String token = "1231531315313";//request.getHeader("token");
        String eId = "1521553153";//request.getHeader("eId");
        if (!StringUtils.hasLength(token) || !StringUtils.hasLength(eId)) {
            log.error("token or employeeId is null");
            SendResponseUtil.sendResponse(response, new Result(4000, "当前账号未登录，请重新登录。如非本人操作，请及时修改密码。"));
            return false;
        }
        Result result = checkTokenAndEmployee(eId, token);
        if (result.getStatus() != 200) {
            SendResponseUtil.sendResponse(response, result);
            return false;
        }
        // 刷新token 过期时间
        return true;

    }

    private Result checkTokenAndEmployee(String eId, String token) {
        // 验证 用户请求头信息
        return new Result();
    }
}
