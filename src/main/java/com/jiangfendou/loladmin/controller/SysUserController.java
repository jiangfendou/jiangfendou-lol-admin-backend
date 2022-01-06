package com.jiangfendou.loladmin.controller;


import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.model.request.DeleteUserBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteUserRequest;
import com.jiangfendou.loladmin.model.request.RepassUserRequest;
import com.jiangfendou.loladmin.model.request.SaveUserRequest;
import com.jiangfendou.loladmin.model.request.SearchUserRequest;
import com.jiangfendou.loladmin.model.request.UpdatePasswordRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserAvatarRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRoleRequest;
import com.jiangfendou.loladmin.service.SysUserRoleService;
import com.jiangfendou.loladmin.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/user-index/{userId}")
    public ApiResponse getUserIndex(@PathVariable Long userId) {
        return ApiResponse.success(sysUserService.getUserIndex(userId));
    }

    @GetMapping("/list")
    public ApiResponse searchUser(SearchUserRequest searchUserRequest) {
        return ApiResponse.success(sysUserService.searchUser(searchUserRequest));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('admin')")
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
    @PreAuthorize("hasRole('admin')")
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
    public ApiResponse updateUserRole(@RequestBody @Validated UpdateUserRoleRequest updateUserRoleRequest)
        throws BusinessException {
        sysUserRoleService.updateUserRole(updateUserRoleRequest);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete-batch")
    public ApiResponse deleteUserBatch(@RequestBody @Validated DeleteUserBatchRequest deleteUserBatchRequest)
        throws BusinessException {
        sysUserService.deleteUserBatch(deleteUserBatchRequest);
        return ApiResponse.success();
    }

    @PutMapping("/update-password")
    public ApiResponse updatePassword(@RequestBody @Validated UpdatePasswordRequest updatePasswordRequest)
        throws BusinessException {
        sysUserService.updatePassword(updatePasswordRequest);
        return ApiResponse.success();
    }

    @PutMapping("/update-upload")
    public ApiResponse uploadImage(@Validated UpdateUserAvatarRequest updateUserAvatarRequest) throws BusinessException {
        sysUserService.uploadImage(updateUserAvatarRequest);
        return ApiResponse.success();
    }

}
