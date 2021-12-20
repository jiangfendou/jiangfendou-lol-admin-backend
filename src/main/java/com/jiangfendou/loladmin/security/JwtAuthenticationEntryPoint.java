package com.jiangfendou.loladmin.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * JwtAuthenticationEntryPoint.
 * @author jiangmh
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        log.error("您还未登录，请先登录：-----------{}", ErrorCodeEnum.NOT_LOGIN_ERROR.getMessage());
        ApiResponse<Object> failedResponse =
            ApiResponse.failed(HttpStatus.UNAUTHORIZED,
                new ApiError(ErrorCodeEnum.NOT_LOGIN_ERROR.getCode(),
                    ErrorCodeEnum.NOT_LOGIN_ERROR.getMessage()));
        JSONObject jsonFailed = new JSONObject(JSONUtil.toJsonStr(failedResponse));
        String httpStatus = (String)jsonFailed.get("httpStatus");
        String[] arraySuccess = httpStatus.split(" ");
        jsonFailed.set("httpStatus", arraySuccess[1]);
        servletOutputStream.write(JSONUtil.toJsonStr(jsonFailed).getBytes("UTF-8"));
        servletOutputStream.flush();
        servletOutputStream.close();

    }
}
