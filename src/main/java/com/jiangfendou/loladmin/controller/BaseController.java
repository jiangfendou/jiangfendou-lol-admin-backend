package com.jiangfendou.loladmin.controller;


import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
public class BaseController {

    @Autowired
    private HttpServletRequest httpServletRequest;


}
