package com.jiangfendou.loladmin.security;

import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.LoginException;
import com.jiangfendou.loladmin.common.Const;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.util.RedisUtil;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * CaptchaFilter.
 * @author jiangmh
 */
@Slf4j
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    private static final String URL = "/login";

    private static final String METHOD = "POST";

    private static final String CODE = "code";

    private static final String KEY = "token";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();
        if (Objects.equals(URL, requestURI) && Objects.equals(METHOD, method)) {
            try {
                // 检验验证码
                validate(httpServletRequest);
            } catch (LoginException loginException) {
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, loginException);
                 return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * 校验验证码
     * */
    private void validate(HttpServletRequest httpServletRequest) {
        String code = httpServletRequest.getParameter(CODE);
        String key = httpServletRequest.getParameter(KEY);
        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            log.error("code和key不能为空");
            httpServletRequest.setAttribute("errorCode", ErrorCodeEnum.CODE_ERROR.getCode());
            httpServletRequest.setAttribute("errorMessage", ErrorCodeEnum.CODE_ERROR.getMessage());
            throw new LoginException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.CODE_ERROR.getCode(),
                    ErrorCodeEnum.CODE_ERROR.getMessage()));
        }

        Object codeRedis = redisUtil.hget(Const.CAPTCHA_KEY, key);
        if (!Objects.equals(code, codeRedis)) {
            log.error("code验证失败：code = {}, redisCode = {}, key = {}", code, codeRedis, key);
            httpServletRequest.setAttribute("errorCode", ErrorCodeEnum.CODE_ERROR.getCode());
            httpServletRequest.setAttribute("errorMessage", ErrorCodeEnum.CODE_ERROR.getMessage());
            throw new LoginException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.CODE_ERROR.getCode(),
                    ErrorCodeEnum.CODE_ERROR.getMessage()));
        }
        // 一次性使用
        redisUtil.hdel(Const.CAPTCHA_KEY, key);
    }
}