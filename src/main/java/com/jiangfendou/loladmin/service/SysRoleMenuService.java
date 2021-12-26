package com.jiangfendou.loladmin.service;

import com.jiangfendou.loladmin.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.loladmin.model.request.UpdateRoleMenuRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * updateRoleMenu()s
     * @param updateRoleMenuRequest updateRoleMenuRequest
     * */
    void updateRoleMenu(UpdateRoleMenuRequest updateRoleMenuRequest);
}
