package com.jiangfendou.loladmin.service;

import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.loladmin.model.request.UpdateUserRoleRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * updateUserRole()
     * @param updateUserRoleRequest updateUserRoleRequest
     * @throws BusinessException BusinessException
     * */
    void updateUserRole(UpdateUserRoleRequest updateUserRoleRequest);
}
