package com.jiangfendou.loladmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.loladmin.model.request.DeleteRoleBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteRoleRequest;
import com.jiangfendou.loladmin.model.request.SaveRoleRequest;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
import com.jiangfendou.loladmin.model.request.UpdateRoleRequest;
import com.jiangfendou.loladmin.model.response.GetRoleDetailResponse;
import com.jiangfendou.loladmin.model.response.SearchRoleResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * searchRole()
     * @param searchRoleRequest searchRoleRequest
     * @return IPage<SearchRoleResponse> IPage<SearchRoleResponse>
     * */
    IPage<SearchRoleResponse> searchRole(SearchRoleRequest searchRoleRequest);

    /**
     * detailRole()
     * @param roleId roleId
     * @return GetRoleDetailResponse GetRoleDetailResponse
     * @throws BusinessException BusinessException
     * */
    GetRoleDetailResponse detailRole(Long roleId) throws BusinessException;

    /**
     * saveRole()
     * @param saveRoleRequest saveRoleRequest
     * @throws BusinessException BusinessException
     * */
    void saveRole(SaveRoleRequest saveRoleRequest) throws BusinessException;

    /**
     * updateRole()
     * @param updateRoleRequest updateRoleRequest
     * @throws BusinessException BusinessException
     * */
    void updateRole(UpdateRoleRequest updateRoleRequest) throws BusinessException;

    /**
     * deleteRole()
     * @param deleteRoleRequest deleteRoleRequest
     * @throws BusinessException BusinessException
     * */
    void deleteRole(DeleteRoleRequest deleteRoleRequest) throws BusinessException;

    /**
     * deleteBatchRole()
     * @param deleteRoleBatchRequest deleteRoleBatchRequest
     * @throws BusinessException BusinessException
     * */
    void deleteBatchRole(DeleteRoleBatchRequest deleteRoleBatchRequest) throws BusinessException;
}
