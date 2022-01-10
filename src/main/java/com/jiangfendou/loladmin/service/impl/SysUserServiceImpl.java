package com.jiangfendou.loladmin.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysMenu;
import com.jiangfendou.loladmin.entity.SysRole;
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
import com.jiangfendou.loladmin.model.request.UpdatePasswordRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserAvatarRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRequest;
import com.jiangfendou.loladmin.model.response.GetUserDetailResponse;
import com.jiangfendou.loladmin.model.response.RoleResponse;
import com.jiangfendou.loladmin.model.response.SearchUserResponse;
import com.jiangfendou.loladmin.model.response.SysUserIndexResponse;
import com.jiangfendou.loladmin.model.response.SysUserResponse;
import com.jiangfendou.loladmin.service.SysMenuService;
import com.jiangfendou.loladmin.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.loladmin.util.QiniuCloudUtil;
import com.jiangfendou.loladmin.util.RedisUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private QiniuCloudUtil qiniuCloudUtil;

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
            log.info("redis获取用户信息 -------{}, Authority = {}", GRANTED_AUTHORITY + sysUser.getId(), authority);
        } else {
            // 获取角色
            List<RoleResponse> roles = sysRoleMapper.searchRoleList(userId);
            if (!roles.isEmpty()) {
                 authority = roles.stream().map(role -> ROLE + role.getCode())
                    .collect(Collectors.joining(","));
            }
            // 获取菜单权限
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
        log.info("删除redis数据，userId = {}" , userId);
        redisUtil.del(GRANTED_AUTHORITY + userId);
        redisUtil.del(USER_MENU + userId);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long rolId) {
        log.info("删除redis数据，rolId = {}" , rolId);
        List<SysUser> sysUsers = sysUserMapper.searchUserList(rolId);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper
            .selectList(new QueryWrapper<SysUserRole>().eq("role_id", rolId)
                .eq("is_deleted", DeletedEnum.NOT_DELETED));
        List<Long> userIds =
            sysUserRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList()).stream().distinct().collect(
                Collectors.toList());
        userIds.forEach(sysUserRole -> {
            redisUtil.del(GRANTED_AUTHORITY + userIds);
            redisUtil.del(USER_MENU + userIds);
        });
    }


    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        log.info("删除redis数据，menuId = {}" , menuId);
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
            log.info("没有找到的指定用户信息：userId = {}", userId);
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        return SysUserResponse.convert(sysUser);
    }

    @Override
    public SysUserIndexResponse getUserIndex(Long userId) {
        SysUser sysUser = this.getOne(
            new QueryWrapper<SysUser>().eq("id", userId)
                .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        List<SysUserRole> sysUserRoles =
            sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>().eq("user_id", userId));
        List<Long> roleIds = sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> sysRoles = sysRoleMapper.selectList(new QueryWrapper<SysRole>().in("id", roleIds));
        List<String> roleNames =
            sysRoles.stream().map(SysRole::getName).collect(Collectors.toList());
        SysUserIndexResponse sysUserIndexResponse = new SysUserIndexResponse();
        BeanUtils.copyProperties(sysUser, sysUserIndexResponse);
        sysUserIndexResponse.setRoleNames(roleNames);
        return sysUserIndexResponse;
    }

    @Override
    public IPage<SearchUserResponse> searchUser(SearchUserRequest searchUserRequest) {
        // 创建分页参数
        IPage<SearchUserResponse> page = new Page<>(searchUserRequest.getCurrent(),searchUserRequest.getSize());
        return sysUserMapper.searchUser(page, searchUserRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SaveUserRequest saveUserRequest) throws BusinessException {

        // 检验用户名称是否存在
        SysUser user = this.getOne(new QueryWrapper<SysUser>().eq("username", saveUserRequest.getUsername())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (!Objects.isNull(user)) {
            log.info("用户名称已经存在：username = {}", saveUserRequest.getUsername());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.USERNAME_EXIST_ERROR.getCode(),
                    String.format(ErrorCodeEnum.USERNAME_EXIST_ERROR.getMessage(), saveUserRequest.getUsername())));
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(saveUserRequest, sysUser);
        // 默认密码
        sysUser.setPassword((sysUser.getPassword() == null || Objects.equals(sysUser.getPassword(), ""))
            ? bCryptPasswordEncoder.encode(DEFAULT_PASSWORD) :  bCryptPasswordEncoder.encode(sysUser.getPassword()));

        // 默认头像
        sysUser.setAvatar(DEFAULT_AVATAR);
        this.save(sysUser);
    }

    @Override
    public GetUserDetailResponse detailUser(Long userId) throws BusinessException {
        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("id", userId)
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("没有找到的指定用户信息：userId = {}", userId);
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
            log.info("用户名称已经存在：username = {}", updateUserRequest.getUsername());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.USERNAME_EXIST_ERROR.getCode(),
                    String.format(ErrorCodeEnum.USERNAME_EXIST_ERROR.getMessage(), updateUserRequest.getUsername())));
        }
        user = new SysUser();
        BeanUtils.copyProperties(updateUserRequest, user);
        if (!this.updateById(user)) {
            log.info("updateUser() ---目标数据已经被锁定， userId = {}", updateUserRequest.getId());
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
            log.info("没有找到的指定用户信息：userId = {}", deleteUserRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        // 删除用户信息
        sysUser = new SysUser();
        sysUser.setIsDeleted(DeletedEnum.DELETED.getValue());
        sysUser.setDeleteDatetime(LocalDateTime.now());
        sysUser.setLockVersion(deleteUserRequest.getLockVersion());
        sysUser.setId(deleteUserRequest.getId());
        if (!this.updateById(sysUser)) {
            log.info("deleteUser() ---目标数据已经被锁定， userId = {}", deleteUserRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
        // 删除用户权限信息表
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setIsDeleted(DeletedEnum.DELETED.getValue());
        sysUserRoleMapper.update(sysUserRole, new QueryWrapper<SysUserRole>()
            .eq("user_id", deleteUserRequest.getId()));

        // 删除redis 缓存
        this.clearUserAuthorityInfo(deleteUserRequest.getId());
    }

    @Override
    public void repassUser(RepassUserRequest repassUserRequest) throws BusinessException {
        SysUser sysUser = this.getOne(
            new QueryWrapper<SysUser>().eq("id", repassUserRequest.getId())
                .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("没有找到的指定用户信息：userId = {}", repassUserRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        // 默认密码
        sysUser = new SysUser();
        sysUser.setPassword((repassUserRequest.getPassword() == null || Objects.equals(repassUserRequest.getPassword(), ""))
            ? bCryptPasswordEncoder.encode(DEFAULT_PASSWORD) :  bCryptPasswordEncoder.encode(repassUserRequest.getPassword()));
        sysUser.setId(repassUserRequest.getId());
        sysUser.setLockVersion(repassUserRequest.getLockVersion());
        if (!this.updateById(sysUser)) {
            log.info("repassUser() ---目标数据已经被锁定， userId = {}", repassUserRequest.getId());
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
            log.info("deleteUserBatch() ---目标数据已经被锁定， userNames = {}", userNames);
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED_BATCH_ERROR.getCode(),
                    String.format(ErrorCodeEnum.LOCKED_BATCH_ERROR.getMessage(), userNames)));
        }
        List<Long> deleteUserIds = deleteUserBatch.stream().map(DeleteUserBatchRequest.DeleteUser::getUserId)
            .collect(Collectors.toList());
        sysUserRoleMapper.deleteUserRoleBatch(deleteUserIds);

        // 删除redis 缓存
        deleteUserIds.forEach(this::clearUserAuthorityInfo);
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) throws BusinessException {
        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("id", updatePasswordRequest.getId())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("没有找到的指定用户信息：userId = {}", updatePasswordRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));

        }
        if (!bCryptPasswordEncoder.matches(updatePasswordRequest.getCurrentPass(), sysUser.getPassword())) {
            log.info("updatePassword() 旧密码输入错误：userId = {}", updatePasswordRequest.getId());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiError(ErrorCodeEnum.OLD_PASSWORD_ERROR.getCode(), ErrorCodeEnum.OLD_PASSWORD_ERROR.getMessage()));
        }
        if (!Objects.equals(updatePasswordRequest.getPassword(), updatePasswordRequest.getCheckPass())) {
            log.info("updatePassword() 确认密码输入错误：userId = {}", updatePasswordRequest.getId());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiError(ErrorCodeEnum.CONFIRM_PASSWORD_ERROR.getCode(), ErrorCodeEnum.CONFIRM_PASSWORD_ERROR.getMessage()));
        }
        sysUser = new SysUser();
        sysUser.setId(updatePasswordRequest.getId());
        sysUser.setPassword(bCryptPasswordEncoder.encode(updatePasswordRequest.getPassword()));
        sysUser.setLockVersion(updatePasswordRequest.getLockVersion());
        this.updateById(sysUser);
    }

    @Override
    public void uploadImage(UpdateUserAvatarRequest updateUserAvatarRequest) throws BusinessException {
        SysUser sysUser = this.getOne(new QueryWrapper<SysUser>().eq("id", updateUserAvatarRequest.getId())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysUser)) {
            log.info("没有找到的指定用户信息：userId = {}", updateUserAvatarRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        if (StringUtils.isNotBlank(sysUser.getAvatarName())) {
            qiniuCloudUtil.delete(sysUser.getAvatarName());
        }
        String url = null;
        String imageName = null;
        try {
            byte[] bytes = updateUserAvatarRequest.getMultipartFile().getBytes();
            imageName = UUID.randomUUID().toString();
            //使用base64方式上传到七牛云
            url = qiniuCloudUtil.put64image(bytes, imageName);
        } catch (Exception e) {
            log.info("uploadImage() 图片上传失败");
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiError(ErrorCodeEnum.UPLOAD_IMAGE_ERROR.getCode(), ErrorCodeEnum.UPLOAD_IMAGE_ERROR.getMessage()));
        }
        sysUser = new SysUser();
        sysUser.setAvatar(url);
        sysUser.setId(updateUserAvatarRequest.getId());
        sysUser.setLockVersion(updateUserAvatarRequest.getLockVersion());
        sysUser.setAvatarName(imageName);
        if (!this.updateById(sysUser)) {
            log.info("repassUser() ---目标数据已经被锁定， userId = {}", updateUserAvatarRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
    }
}
