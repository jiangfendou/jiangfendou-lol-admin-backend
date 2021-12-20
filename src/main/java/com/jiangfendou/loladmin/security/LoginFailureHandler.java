package com.jiangfendou.loladmin.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.LoginException;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;


/**
 * LoginFailureHandler.
 * @author jiangmh
 */
@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        ApiResponse<Object> failedResponse = null;
        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            log.error("账户密码或者密码输入错误：-----------{}", ErrorCodeEnum.ACCOUNT_PASSWORD_ERROR.getMessage());
            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCodeEnum.ACCOUNT_PASSWORD_ERROR.getCode(),
                ErrorCodeEnum.ACCOUNT_PASSWORD_ERROR.getMessage()));
        }
//        else if (e instanceof LockedException) {
//            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCode.ACCOUNT_PASSWORD_ERROR.getCode(),
//                ErrorCode.ACCOUNT_PASSWORD_ERROR.getMessage()));
//        } else if (e instanceof CredentialsExpiredException) {
//            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCode.ACCOUNT_PASSWORD_ERROR.getCode(),
//                ErrorCode.ACCOUNT_PASSWORD_ERROR.getMessage()));
//        } else if (e instanceof AccountExpiredException) {
//            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCode.ACCOUNT_PASSWORD_ERROR.getCode(),
//                ErrorCode.ACCOUNT_PASSWORD_ERROR.getMessage()));
//        } else if (e instanceof DisabledException) {
//            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCode.ACCOUNT_PASSWORD_ERROR.getCode(),
//                ErrorCode.ACCOUNT_PASSWORD_ERROR.getMessage()));
//        }
        else if (e instanceof LoginException) {
            log.error("验证码输入错误：-----------{}", ErrorCodeEnum.CODE_ERROR.getMessage());
            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCodeEnum.CODE_ERROR.getCode(),
                ErrorCodeEnum.CODE_ERROR.getMessage()));
        }
        else {
            log.error("验证码输入错误：-----------{}", ErrorCodeEnum.CODE_ERROR.getMessage());
            failedResponse = ApiResponse.failed(HttpStatus.BAD_REQUEST, new ApiError(ErrorCodeEnum.ACCOUNT_PASSWORD_ERROR.getCode(),
                ErrorCodeEnum.ACCOUNT_PASSWORD_ERROR.getMessage()));
        }
        JSONObject jsonFailed = new JSONObject(JSONUtil.toJsonStr(failedResponse));
        String httpStatus = (String)jsonFailed.get("httpStatus");
        String[] arraySuccess = httpStatus.split(" ");
        jsonFailed.set("httpStatus", arraySuccess[1]);
        servletOutputStream.write(JSONUtil.toJsonStr(jsonFailed).getBytes("UTF-8"));
        servletOutputStream.flush();
        servletOutputStream.close();

    }
}
