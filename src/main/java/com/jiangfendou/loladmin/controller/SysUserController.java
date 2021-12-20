package com.jiangfendou.loladmin.controller;


import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.model.request.DeleteUserBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteUserRequest;
import com.jiangfendou.loladmin.model.request.RepassUserRequest;
import com.jiangfendou.loladmin.model.request.SaveUserRequest;
import com.jiangfendou.loladmin.model.request.SearchUserRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRoleRequest;
import com.jiangfendou.loladmin.service.SysUserRoleService;
import com.jiangfendou.loladmin.service.SysUserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/userInfo")
    public ApiResponse getUserInfo(Long userId) throws BusinessException {
        return ApiResponse.success(sysUserService.getUserInfo(userId));
    }

    @GetMapping("/list")
    public ApiResponse searchUser(SearchUserRequest searchUserRequest) {
        return ApiResponse.success(sysUserService.searchUser(searchUserRequest));

    }

    @PostMapping("/save")
    public ApiResponse saveUser(@RequestBody @Validated SaveUserRequest searchUserRequest) throws BusinessException {
        sysUserService.saveUser(searchUserRequest);
        return ApiResponse.success();
    }

    @GetMapping("/detail")
    public ApiResponse detailUser(Long userId) throws BusinessException {
        return ApiResponse.success(sysUserService.detailUser(userId));
    }

    @PostMapping("/update")
    public ApiResponse updateUser(@RequestBody @Validated UpdateUserRequest updateUserRequest)
        throws BusinessException {
        sysUserService.updateUser(updateUserRequest);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete")
    public ApiResponse deleteUser(@RequestBody @Validated DeleteUserRequest deleteUserRequest)
        throws BusinessException {
        sysUserService.deleteUser(deleteUserRequest);
        return ApiResponse.success();
    }

    @PutMapping("/repass")
    public ApiResponse repassUser(@RequestBody @Validated RepassUserRequest repassUserRequest)
        throws BusinessException {
        sysUserService.repassUser(repassUserRequest);
        return ApiResponse.success();
    }

    @PostMapping("/update-user-role")
    public ApiResponse updateUserRole(@RequestBody @Validated UpdateUserRoleRequest updateUserRoleRequest) {
        sysUserRoleService.updateUserRole(updateUserRoleRequest);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete-batch")
    public ApiResponse deleteUserBatch(@RequestBody @Validated DeleteUserBatchRequest deleteUserBatchRequest)
        throws BusinessException {
        sysUserService.deleteUserBatch(deleteUserBatchRequest);
        return ApiResponse.success();
    }

}
