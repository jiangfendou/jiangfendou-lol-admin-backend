package com.jiangfendou.loladmin.config;

import com.jiangfendou.loladmin.security.CaptchaFilter;
import com.jiangfendou.loladmin.security.JwtAccessDeniedHandler;
import com.jiangfendou.loladmin.security.JwtAuthenticationEntryPoint;
import com.jiangfendou.loladmin.security.JwtAuthenticationFilter;
import com.jiangfendou.loladmin.security.JwtLogoutSuccessHandler;
import com.jiangfendou.loladmin.security.LoginFailureHandler;
import com.jiangfendou.loladmin.security.LoginSuccessHandler;
import com.jiangfendou.loladmin.security.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    private static final String[] URL_WHITE_LIST = {
        "/login",
        "/logout",
        "/captcha",
        "/favicon.ico"
    };

    @Autowired
    private MyDisableUrlSessionFilter myDisableUrlSessionFilter;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private CaptchaFilter captchaFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
            .addFilterBefore(myDisableUrlSessionFilter,UsernamePasswordAuthenticationFilter.class)
            // 登陆配置
            .formLogin()
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
            // 退出成功
        .and()
            .logout()
            .logoutSuccessHandler(jwtLogoutSuccessHandler)
            // 禁用session
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // 拦截规则
        .and()
            .authorizeRequests()
            .antMatchers(URL_WHITE_LIST).permitAll()
            .anyRequest().authenticated()
            // 异常处理器
        .and()
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
            // 配置定义的过滤器
        .and()
            .addFilter(jwtAuthenticationFilter())
            // 配置验证码过滤器要在密码验证其之前拦截
            .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }
}
