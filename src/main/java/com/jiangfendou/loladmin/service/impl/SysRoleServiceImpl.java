package com.jiangfendou.loladmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysRole;
import com.jiangfendou.loladmin.entity.SysRoleMenu;
import com.jiangfendou.loladmin.enums.DeletedEnum;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.mapper.SysRoleMapper;
import com.jiangfendou.loladmin.mapper.SysRoleMenuMapper;
import com.jiangfendou.loladmin.model.request.SaveRoleRequest;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
import com.jiangfendou.loladmin.model.response.GetRoleDetailResponse;
import com.jiangfendou.loladmin.model.response.SearchRoleResponse;
import com.jiangfendou.loladmin.service.SysRoleService;
import java.sql.Array;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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


    @Override
    public IPage<SearchRoleResponse> searchRole(SearchRoleRequest searchRoleRequest) {
        IPage<SearchRoleResponse> page = new Page<>(searchRoleRequest.getCurrent(),searchRoleRequest.getSize());
        return sysRoleMapper.searchRole(page, searchRoleRequest);
    }

    @Override
    public GetRoleDetailResponse detailRole(Long roleId) {
        GetRoleDetailResponse getRoleDetailResponse = sysRoleMapper.detailRole(roleId);
        List<SysRoleMenu> sysRoleMenus =
            sysRoleMenuMapper.selectList(new QueryWrapper<SysRoleMenu>().eq("menu_id", roleId)
                .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        List<Long> menuIdList =
            sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        getRoleDetailResponse.setMenuIds(menuIdList);
        return getRoleDetailResponse;
    }

    @Override
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
}
