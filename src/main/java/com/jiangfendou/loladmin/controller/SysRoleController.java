package com.jiangfendou.loladmin.controller;


import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
import com.jiangfendou.loladmin.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public ApiResponse searchRole(SearchRoleRequest searchRoleRequest){
        return ApiResponse.success(sysRoleService.searchRole(searchRoleRequest));
    }

    @GetMapping("/detail")
    public ApiResponse detailRole(Long roleId){
        return ApiResponse.success(sysRoleService.detailRole(roleId));
    }



}
