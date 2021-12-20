package com.jiangfendou.loladmin.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.model.response.LoginResponse;
import com.jiangfendou.loladmin.util.JwtUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


/**
 * LoginFailureHandler.
 * @author jiangmh
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        // 生成的jwt， 放置到请求头中
        String jwt = jwtUtils.generateToken(authentication.getName());
        AccountUser accountUser = (AccountUser) authentication.getPrincipal();
        httpServletResponse.setHeader(jwtUtils.getHeader(), jwt);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(jwt);
        loginResponse.setRefreshToken(jwt);
        loginResponse.setUsername(authentication.getName());
        loginResponse.setUserId(accountUser.getId());
        ApiResponse<Object> success = ApiResponse.success(loginResponse);
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
