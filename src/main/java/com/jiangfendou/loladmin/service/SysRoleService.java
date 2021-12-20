package com.jiangfendou.loladmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangfendou.loladmin.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
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
     * */
    GetRoleDetailResponse detailRole(Long roleId);
}
