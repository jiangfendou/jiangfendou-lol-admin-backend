package com.jiangfendou.loladmin.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.util.JwtUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;


/**
 * LoginFailureHandler.
 * @author jiangmh
 */
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);
        }
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setHeader(jwtUtils.getHeader(), "");
        ApiResponse<Object> success = ApiResponse.success();
        String successStr = JSONUtil.toJsonStr(success);
        JSONObject jsonSuccess = new JSONObject(successStr);
        String httpStatus = (String)jsonSuccess.get("httpStatus");
        String[] arraySuccess = httpStatus.split(" ");
        jsonSuccess.set("httpStatus", arraySuccess[1]);
        servletOutputStream.write(JSONUtil.toJsonStr(jsonSuccess).getBytes("UTF-8"));
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}
