package com.jiangfendou.loladmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysUserRole;
import com.jiangfendou.loladmin.mapper.SysUserRoleMapper;
import com.jiangfendou.loladmin.model.request.UpdateUserRoleRequest;
import com.jiangfendou.loladmin.service.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    public void updateUserRole(UpdateUserRoleRequest updateUserRoleRequest) {
        // 删除当前用户下的所有角色
        this.remove(new QueryWrapper<SysUserRole>().eq("user_id", updateUserRoleRequest.getUserId()));
        // 添加当前用户下的所有权限
        List<SysUserRole> sysUserRoles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updateUserRoleRequest.getRoleIds())) {
            sysUserRoles = updateUserRoleRequest.getRoleIds().stream().map(roleId -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(updateUserRoleRequest.getUserId());
                return sysUserRole;
            }).collect(Collectors.toList());
        }
        this.saveBatch(sysUserRoles);
    }
}
