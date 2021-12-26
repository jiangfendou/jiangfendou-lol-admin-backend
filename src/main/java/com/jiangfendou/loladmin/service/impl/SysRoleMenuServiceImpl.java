package com.jiangfendou.loladmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiangfendou.loladmin.entity.SysRole;
import com.jiangfendou.loladmin.entity.SysRoleMenu;
import com.jiangfendou.loladmin.entity.SysUserRole;
import com.jiangfendou.loladmin.enums.DeletedEnum;
import com.jiangfendou.loladmin.mapper.SysRoleMenuMapper;
import com.jiangfendou.loladmin.model.request.UpdateRoleMenuRequest;
import com.jiangfendou.loladmin.service.SysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.loladmin.service.SysUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void updateRoleMenu(UpdateRoleMenuRequest updateRoleMenuRequest) {
        List<SysRoleMenu> roleMenus =
            this.list(new QueryWrapper<SysRoleMenu>().eq("role_id",
                updateRoleMenuRequest.getRoleId()).eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (!CollectionUtils.isEmpty(roleMenus)) {
            this.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",
                updateRoleMenuRequest.getRoleId()).eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        }
        // 添加当前用户下的所有权限
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updateRoleMenuRequest.getMenuIds())) {
            sysRoleMenus = updateRoleMenuRequest.getMenuIds().stream().map(menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(updateRoleMenuRequest.getRoleId());
                sysRoleMenu.setMenuId(menuId);
                return sysRoleMenu;
            }).collect(Collectors.toList());
        }

        if (this.saveBatch(sysRoleMenus)) {
            // 清楚 redis 缓存
            sysUserService.clearUserAuthorityInfoByRoleId(updateRoleMenuRequest.getRoleId());
        }
    }
}
