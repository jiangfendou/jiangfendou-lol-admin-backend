package com.jiangfendou.loladmin.security;

import cn.hutool.core.util.StrUtil;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.TokenException;
import com.jiangfendou.loladmin.entity.SysUser;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.service.SysUserService;
import com.jiangfendou.loladmin.util.JwtUtils;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * JwtAuthenticationFilter.
 * @author jiangmh
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private SysUserService sysUserService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String jwt = request.getHeader(jwtUtils.getHeader());
        if (StrUtil.isNullOrUndefined(jwt)) {
            chain.doFilter(request, response);
            return;
        }
        Claims claims = null;
        try {
            claims = checkToken(jwt);
        } catch (TokenException tokenException) {
            jwtAccessDeniedHandler.handle(request, response, tokenException);
        }


        String username = claims.getSubject();
        // 获取用户的权限信息----security 自动登录
        SysUser sysUser = sysUserService.getByUseName(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null
        , userDetailService.getUserAuthority(sysUser.getId()));
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }

    private Claims checkToken(String jwt) {
        Claims claimByToken = jwtUtils.getClaimByToken(jwt);

        if (claimByToken == null) {
            throw new TokenException(HttpStatus.UNAUTHORIZED,
                new ApiError(ErrorCodeEnum.INVALID_TOKEN.getCode(),
                    ErrorCodeEnum.INVALID_TOKEN.getMessage()));
        }

        if (jwtUtils.isTokenExpired(claimByToken)) {
            throw new TokenException(HttpStatus.UNAUTHORIZED,
                new ApiError(ErrorCodeEnum.EXPIRED_TOKEN.getCode(),
                    ErrorCodeEnum.EXPIRED_TOKEN.getMessage()));
        }
        return claimByToken;
    }
}
