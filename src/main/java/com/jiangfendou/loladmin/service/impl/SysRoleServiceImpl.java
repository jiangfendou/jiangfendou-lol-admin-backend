package com.jiangfendou.loladmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysRole;
import com.jiangfendou.loladmin.entity.SysUserRole;
import com.jiangfendou.loladmin.enums.DeletedEnum;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.mapper.SysRoleMapper;
import com.jiangfendou.loladmin.mapper.SysRoleMenuMapper;
import com.jiangfendou.loladmin.mapper.SysUserRoleMapper;
import com.jiangfendou.loladmin.model.request.DeleteRoleBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteRoleRequest;
import com.jiangfendou.loladmin.model.request.SaveRoleRequest;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
import com.jiangfendou.loladmin.model.request.UpdateRoleRequest;
import com.jiangfendou.loladmin.model.response.GetRoleDetailResponse;
import com.jiangfendou.loladmin.model.response.SearchRoleResponse;
import com.jiangfendou.loladmin.service.SysRoleService;
import com.jiangfendou.loladmin.service.SysUserRoleService;
import com.jiangfendou.loladmin.service.SysUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserService sysUserService;


    @Override
    public IPage<SearchRoleResponse> searchRole(SearchRoleRequest searchRoleRequest) {
        IPage<SearchRoleResponse> page = new Page<>(searchRoleRequest.getCurrent(),searchRoleRequest.getSize());
        return sysRoleMapper.searchRole(page, searchRoleRequest);
    }

    @Override
    public GetRoleDetailResponse detailRole(Long roleId) throws BusinessException {
        SysRole sysRole =
            this.getOne(new QueryWrapper<SysRole>().eq("id", roleId)
                .eq("is_deleted", DeletedEnum.NOT_DELETED));
        if (Objects.isNull(sysRole)) {
            log.info("detailRole() ---目标数据没有被找到， roleId = {}", roleId);
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        GetRoleDetailResponse getRoleDetailResponse = new GetRoleDetailResponse();
        BeanUtils.copyProperties(sysRole, getRoleDetailResponse);
        return getRoleDetailResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(SaveRoleRequest saveRoleRequest) throws BusinessException {
        // 检验权限编码是否已经存在
        SysRole sysRole = this.getOne(new QueryWrapper<SysRole>().eq("code", saveRoleRequest.getCode())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (!Objects.isNull(sysRole)) {
            log.info("权限编码已存在：role code = {}", saveRoleRequest.getCode());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.ROLE_CODE_EXITS_ERROR.getCode(),
                    String.format(ErrorCodeEnum.ROLE_CODE_EXITS_ERROR.getMessage(), saveRoleRequest.getCode())));
        }
        sysRole = new SysRole();
        BeanUtils.copyProperties(saveRoleRequest, sysRole);
        this.save(sysRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(UpdateRoleRequest updateRoleRequest) throws BusinessException {
        SysRole sysRole = this.getOne(new QueryWrapper<SysRole>().eq("id", updateRoleRequest.getId())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysRole)) {
            log.info("updateRole() ---目标数据没有被找到， roleId = {}", updateRoleRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        // 检验权限编码是否已经存在
        sysRole = this.getOne(new QueryWrapper<SysRole>().eq("code", updateRoleRequest.getCode())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()).ne("id", updateRoleRequest.getId()));
        if (!Objects.isNull(sysRole)) {
            log.info("权限编码已存在：role code = {}", updateRoleRequest.getCode());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.ROLE_CODE_EXITS_ERROR.getCode(),
                    String.format(ErrorCodeEnum.ROLE_CODE_EXITS_ERROR.getMessage(), updateRoleRequest.getCode())));
        }
        sysRole = new SysRole();
        BeanUtils.copyProperties(updateRoleRequest, sysRole);
        if (!this.updateById(sysRole)) {
            log.info("updateMenu() ---目标数据已经被锁定， roleId = {}", updateRoleRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(DeleteRoleRequest deleteRoleRequest) throws BusinessException {
        SysRole sysRole = this.getOne(new QueryWrapper<SysRole>().eq("id", deleteRoleRequest.getId())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysRole)) {
            log.info("deleteRole() ---目标数据没有被找到， roleId = {}", deleteRoleRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }

        // 判断是否有用户使用该权限
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(
            new QueryWrapper<SysUserRole>().eq("role_id", deleteRoleRequest.getId())
                .eq("is_deleted", DeletedEnum.NOT_DELETED));
        if (!CollectionUtils.isEmpty(sysUserRoles)) {
            log.info("deleteRole() ---该权限被用户引用无法删除， roleId = {}", deleteRoleRequest.getId());
            throw new BusinessException(HttpStatus.UNAUTHORIZED,
                new ApiError(ErrorCodeEnum.PERMISSION_CODE_REFERENCED.getCode(),
                    String.format(ErrorCodeEnum.PERMISSION_CODE_REFERENCED.getMessage(), sysRole.getCode())));
        }
        // 删除权限
        sysRole = new SysRole();
        BeanUtils.copyProperties(deleteRoleRequest, sysRole);
        sysRole.setIsDeleted(DeletedEnum.DELETED.getValue());
        if (!this.updateById(sysRole)) {
            log.info("deleteRole() ---目标数据已经被锁定， roleId = {}", deleteRoleRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }

        // 删除redis缓存
        sysUserService.clearUserAuthorityInfoByRoleId(deleteRoleRequest.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchRole(DeleteRoleBatchRequest deleteRoleBatchRequest) throws BusinessException {
        List<DeleteRoleBatchRequest.DeleteRole> deleteRoleBatch = deleteRoleBatchRequest.getDeleteRoleBatch();
        // 被锁定的集合
        List<Long> lockedRoleIds = new ArrayList<>();
        // 被引用的集合
        List<Long> referencedRoleIds = new ArrayList<>();
        deleteRoleBatch.forEach(deleteUser -> {
            // 判断是否有用户使用该权限
            List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(
                new QueryWrapper<SysUserRole>().eq("role_id", deleteUser.getRoleId())
                    .eq("is_deleted", DeletedEnum.NOT_DELETED));
            if (!CollectionUtils.isEmpty(sysUserRoles)) {
                referencedRoleIds.add(deleteUser.getRoleId());
                return;
            }
            SysRole sysRole = new SysRole();
            sysRole.setId(deleteUser.getRoleId());
            sysRole.setIsDeleted(DeletedEnum.DELETED.getValue());
            sysRole.setLockVersion(deleteUser.getLockVersion());
            if (!this.updateById(sysRole)) {
                lockedRoleIds.add(deleteUser.getRoleId());
            }
        });

        if (!CollectionUtils.isEmpty(referencedRoleIds)) {
            List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(referencedRoleIds);
            List<String> roleName = sysRoles.stream().map(SysRole::getName).collect(Collectors.toList());
            log.info("deleteBatchRole() ---目标数据被引用无法删除， role name = {}", roleName);
            throw new BusinessException(HttpStatus.UNAUTHORIZED,
                new ApiError(ErrorCodeEnum.PERMISSION_CODE_REFERENCED.getCode(),
                    String.format(ErrorCodeEnum.PERMISSION_CODE_REFERENCED.getMessage(), roleName)));
        }

        if (!CollectionUtils.isEmpty(lockedRoleIds)) {
            List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(lockedRoleIds);
            List<String> roleName = sysRoles.stream().map(SysRole::getName).collect(Collectors.toList());
            log.info("deleteBatchRole() ---目标数据已经被锁定， role name = {}", roleName);
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED_BATCH_ERROR.getCode(),
                    String.format(ErrorCodeEnum.LOCKED_BATCH_ERROR.getMessage(), roleName)));
        }
        List<Long> deleteRoleIds = deleteRoleBatch.stream().map(DeleteRoleBatchRequest.DeleteRole::getRoleId)
            .collect(Collectors.toList());
        sysRoleMenuMapper.deleteRoleMenuBatch(deleteRoleIds);

        // 删除redis 缓存
        deleteRoleIds.forEach(roleId -> {
            sysUserService.clearUserAuthorityInfoByRoleId(roleId);
        });
    }
}
