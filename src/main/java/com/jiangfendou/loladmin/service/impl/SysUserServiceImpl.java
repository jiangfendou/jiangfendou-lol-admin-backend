package com.jiangfendou.loladmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysMenu;
import com.jiangfendou.loladmin.entity.SysUser;
import com.jiangfendou.loladmin.entity.SysUserRole;
import com.jiangfendou.loladmin.enums.DeletedEnum;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.mapper.SysMenuMapper;
import com.jiangfendou.loladmin.mapper.SysRoleMapper;
import com.jiangfendou.loladmin.mapper.SysUserMapper;
import com.jiangfendou.loladmin.mapper.SysUserRoleMapper;
import com.jiangfendou.loladmin.model.request.DeleteUserBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteUserRequest;
import com.jiangfendou.loladmin.model.request.RepassUserRequest;
import com.jiangfendou.loladmin.model.request.SaveUserRequest;
import com.jiangfendou.loladmin.model.request.SearchUserRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRequest;
import com.jiangfendou.loladmin.model.response.GetUserDetailResponse;
import com.jiangfendou.loladmin.model.response.RoleResponse;
import com.jiangfendou.loladmin.model.response.SearchUserResponse;
import com.jiangfendou.loladmin.model.response.SysUserResponse;
import com.jiangfendou.loladmin.service.SysMenuService;
import com.jiangfendou.loladmin.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.loladmin.util.RedisUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 *  ???????????????
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final String GRANTED_AUTHORITY = "GrantedAuthority:";

    private static final String USER_MENU = "UserMenu:";

    private static final String ROLE = "ROLE_";

    private static final String DEFAULT_PASSWORD = "888888";

    private static final String DEFAULT_AVATAR = "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png";

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public SysUser getByUseName(String userName) {
        return getOne(new QueryWrapper<SysUser>().eq("username", userName));
    }
    @Override
    public String getUserAuthorityInfo(Long userId) {
        // ROLE_admin,sys:user:list .....
        String authority = "";
        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("id", userId)
            .eq("is_deleted", DeletedEnum.NOT_DELETED));
        if (redisUtil.hasKey(GRANTED_AUTHORITY + sysUser.getId())) {
            authority = (String)redisUtil.get(GRANTED_AUTHORITY + sysUser.getId());
            log.info("redis?????????????????? -------{}, Authority = {}", GRANTED_AUTHORITY + sysUser.getId(), authority);
        } else {
            // ????????????
            List<RoleResponse> roles = sysRoleMapper.searchRoleList(userId);
            if (!roles.isEmpty()) {
                 authority = roles.stream().map(role -> ROLE + role.getCode())
                    .collect(Collectors.joining(","));
            }
            // ??????????????????
            List<Long> menuIds = sysUserMapper.getNavMenusIds(userId);
            if (!menuIds.isEmpty()) {
                List<SysMenu> sysMenus = sysMenuService.listByIds(menuIds);
                String menuPerms = sysMenus.stream().map(SysMenu::getPerms)
                    .collect(Collectors.joining(","));
                authority = authority.concat(",").concat(menuPerms);
            }
            redisUtil.set(GRANTED_AUTHORITY + userId, authority, 60*60);
        }

        return authority;
    }

    @Override
    public void clearUserAuthorityInfo(Long userId) {
        log.info("??????redis?????????userId = {}" , userId);
        redisUtil.del(GRANTED_AUTHORITY + userId);
        redisUtil.del(USER_MENU + userId);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long rolId) {
        log.info("??????redis?????????userId = {}" , rolId);
        List<SysUser> sysUsers = sysUserMapper.searchUserList(rolId);
        sysUsers.forEach(sysUser -> {
            redisUtil.del(GRANTED_AUTHORITY + sysUser.getUsername());
        });
    }


    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        log.info("??????redis?????????menuId = {}" , menuId);
        List<SysUser> sysUsers = sysMenuMapper.searchUserList(menuId);
        sysUsers.forEach(sysUser -> {
            redisUtil.del(GRANTED_AUTHORITY + sysUser.getId());
            redisUtil.del(USER_MENU + sysUser.getId());
        });
    }

    @Override
    public SysUserResponse getUserInfo(Long userId) throws BusinessException {
        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("id", userId)
            .eq("is_deleted", DeletedEnum.NOT_DELETED));
        if (Objects.isNull(sysUser)) {
            log.info("????????????????????????????????????userId = {}", userId);
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        return SysUserResponse.convert(sysUser);
    }

    @Override
    public IPage<SearchUserResponse> searchUser(SearchUserRequest searchUserRequest) {
        // ??????????????????
        IPage<SearchUserResponse> page = new Page<>(searchUserRequest.getCurrent(),searchUserRequest.getSize());
        return sysUserMapper.searchUser(page, searchUserRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SaveUserRequest saveUserRequest) throws BusinessException {

        // ??????????????????????????????
        SysUser user = this.getOne(new QueryWrapper<SysUser>().eq("username", saveUserRequest.getUsername())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (!Objects.isNull(user)) {
            log.info("???????????????????????????username = {}", saveUserRequest.getUsername());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.USERNAME_EXIST_ERROR.getCode(),
                    ErrorCodeEnum.USERNAME_EXIST_ERROR.getMessage()));
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(saveUserRequest, sysUser);
        // ????????????
        sysUser.setPassword((sysUser.getPassword() == null || Objects.equals(sysUser.getPassword(), ""))
            ? bCryptPasswordEncoder.encode(DEFAULT_PASSWORD) :  bCryptPasswordEncoder.encode(sysUser.getPassword()));

        // ????????????
        sysUser.setAvatar(DEFAULT_AVATAR);
        this.save(sysUser);
    }

    @Override
    public GetUserDetailResponse detailUser(Long userId) throws BusinessException {
        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("id", userId)
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("????????????????????????????????????userId = {}", userId);
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        GetUserDetailResponse getUserDetailResponse = new GetUserDetailResponse();
        BeanUtils.copyProperties(sysUser, getUserDetailResponse);
        return getUserDetailResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UpdateUserRequest updateUserRequest) throws BusinessException {

        SysUser user = this.getOne(new QueryWrapper<SysUser>().eq("username", updateUserRequest.getUsername())
            .ne("id", updateUserRequest.getId()).eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (!Objects.isNull(user)) {
            log.info("???????????????????????????username = {}", updateUserRequest.getUsername());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.USERNAME_EXIST_ERROR.getCode(),
                    ErrorCodeEnum.USERNAME_EXIST_ERROR.getMessage()));
        }
        user = new SysUser();
        BeanUtils.copyProperties(updateUserRequest, user);
        if (!this.updateById(user)) {
            log.info("updateUser() ---?????????????????????????????? userId = {}", updateUserRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest deleteUserRequest) throws BusinessException {
        SysUser sysUser = this.getOne(
            new QueryWrapper<SysUser>().eq("id", deleteUserRequest.getId())
                .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("????????????????????????????????????userId = {}", deleteUserRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        // ??????????????????
        sysUser = new SysUser();
        sysUser.setIsDeleted(DeletedEnum.DELETED.getValue());
        sysUser.setDeleteDatetime(LocalDateTime.now());
        sysUser.setLockVersion(deleteUserRequest.getLockVersion());
        sysUser.setId(deleteUserRequest.getId());
        if (!this.updateById(sysUser)) {
            log.info("deleteUser() ---?????????????????????????????? userId = {}", deleteUserRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
        // ???????????????????????????
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setIsDeleted(DeletedEnum.DELETED.getValue());
        sysUserRoleMapper.update(sysUserRole, new QueryWrapper<SysUserRole>()
            .eq("user_id", deleteUserRequest.getId()));

        // ??????redis ??????
        this.clearUserAuthorityInfo(deleteUserRequest.getId());
    }

    @Override
    public void repassUser(RepassUserRequest repassUserRequest) throws BusinessException {
        SysUser sysUser = this.getOne(
            new QueryWrapper<SysUser>().eq("id", repassUserRequest.getId())
                .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("????????????????????????????????????userId = {}", repassUserRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        // ????????????
        sysUser = new SysUser();
        sysUser.setPassword((repassUserRequest.getPassword() == null || Objects.equals(repassUserRequest.getPassword(), ""))
            ? bCryptPasswordEncoder.encode(DEFAULT_PASSWORD) :  bCryptPasswordEncoder.encode(repassUserRequest.getPassword()));
        sysUser.setId(repassUserRequest.getId());
        sysUser.setLockVersion(repassUserRequest.getLockVersion());
        if (!this.updateById(sysUser)) {
            log.info("repassUser() ---?????????????????????????????? userId = {}", repassUserRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
    }

    @Override
    public void deleteUserBatch(DeleteUserBatchRequest deleteUserBatchRequest) throws BusinessException {
        List<DeleteUserBatchRequest.DeleteUser> deleteUserBatch = deleteUserBatchRequest.getDeleteUserBatch();
        List<Long> userIds = new ArrayList<>();
        deleteUserBatch.forEach(deleteUser -> {
            SysUser sysUser = new SysUser();
            sysUser.setId(deleteUser.getUserId());
            sysUser.setIsDeleted(DeletedEnum.DELETED.getValue());
            sysUser.setLockVersion(deleteUser.getLockVersion());
            boolean isSuccess = this.updateById(sysUser);
            if (!isSuccess) {
                userIds.add(deleteUser.getUserId());
            }
        });
        if (!CollectionUtils.isEmpty(userIds)) {
            List<SysUser> sysUsers = sysUserMapper.selectBatchIds(userIds);
            List<String> userNames = sysUsers.stream().map(SysUser::getUsername).collect(Collectors.toList());
            log.info("deleteUserBatch() ---?????????????????????????????? userNames = {}", userNames);
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED_BATCH_ERROR.getCode(),
                    String.format(ErrorCodeEnum.LOCKED_BATCH_ERROR.getMessage(), userNames)));
        }
        List<Long> deleteUserIds = deleteUserBatch.stream().map(DeleteUserBatchRequest.DeleteUser::getUserId)
            .collect(Collectors.toList());
        sysUserRoleMapper.deleteUserRoleBatch(deleteUserIds);

        // ??????redis ??????
        deleteUserIds.forEach(this::clearUserAuthorityInfo);
    }
}
