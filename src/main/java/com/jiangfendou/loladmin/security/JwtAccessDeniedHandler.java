package com.jiangfendou.loladmin.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.TokenException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * JwtAccessDeniedHandler.
 * @author jiangmh
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        TokenException tokenException = (TokenException) e;
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setStatus(tokenException.getHttpStatus().value());
        log.error("Token异常：-----------{}", tokenException.getApiError().getMessage());
        ApiResponse<Object> failedResponse =
            ApiResponse.failed(tokenException.getHttpStatus(), new ApiError(tokenException.getApiError().getCode(),
                tokenException.getApiError().getMessage()));
        JSONObject jsonFailed = new JSONObject(JSONUtil.toJsonStr(failedResponse));
        String httpStatus = (String)jsonFailed.get("httpStatus");
        String[] arraySuccess = httpStatus.split(" ");
        jsonFailed.set("httpStatus", arraySuccess[1]);
        servletOutputStream.write(JSONUtil.toJsonStr(jsonFailed).getBytes("UTF-8"));
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}
