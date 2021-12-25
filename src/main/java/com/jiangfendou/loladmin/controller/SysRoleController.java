package com.jiangfendou.loladmin.controller;


import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.model.request.DeleteRoleBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteRoleRequest;
import com.jiangfendou.loladmin.model.request.SaveRoleRequest;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
import com.jiangfendou.loladmin.model.request.UpdateRoleRequest;
import com.jiangfendou.loladmin.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public ApiResponse searchRole(SearchRoleRequest searchRoleRequest){
        return ApiResponse.success(sysRoleService.searchRole(searchRoleRequest));
    }

    @GetMapping("/detail")
    public ApiResponse detailRole(Long roleId) throws BusinessException {
        return ApiResponse.success(sysRoleService.detailRole(roleId));
    }

    @PostMapping("/save")
    public ApiResponse saveRole(@RequestBody @Validated SaveRoleRequest saveRoleRequest) throws BusinessException {
        sysRoleService.saveRole(saveRoleRequest);
        return ApiResponse.success();
    }

    @PostMapping("/update")
    public ApiResponse updateRole(@RequestBody @Validated UpdateRoleRequest updateRoleRequest)
        throws BusinessException {
        sysRoleService.updateRole(updateRoleRequest);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete")
    public ApiResponse deleteRole(@RequestBody @Validated DeleteRoleRequest deleteRoleRequest)
        throws BusinessException {
        sysRoleService.deleteRole(deleteRoleRequest);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete-batch")
    public ApiResponse deleteBatchRole(@RequestBody @Validated DeleteRoleBatchRequest deleteRoleRequest)
        throws BusinessException {
        sysRoleService.deleteBatchRole(deleteRoleRequest);
        return ApiResponse.success();
    }

}
