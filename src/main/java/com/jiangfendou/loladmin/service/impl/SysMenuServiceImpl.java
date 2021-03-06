package com.jiangfendou.loladmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.loladmin.common.ApiError;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysMenu;
import com.jiangfendou.loladmin.entity.SysRoleMenu;
import com.jiangfendou.loladmin.entity.SysUser;
import com.jiangfendou.loladmin.enums.DeletedEnum;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.enums.StatusEnum;
import com.jiangfendou.loladmin.mapper.SysMenuMapper;
import com.jiangfendou.loladmin.mapper.SysRoleMenuMapper;
import com.jiangfendou.loladmin.model.request.SaveMenuRequest;
import com.jiangfendou.loladmin.model.request.DeleteMenuRequest;
import com.jiangfendou.loladmin.model.request.UpdateMenuRequest;
import com.jiangfendou.loladmin.model.response.GetMenuDetailResponse;
import com.jiangfendou.loladmin.model.response.MenuAuthorityResponse;
import com.jiangfendou.loladmin.model.response.SearchMenusResponse;
import com.jiangfendou.loladmin.service.SysMenuService;
import com.jiangfendou.loladmin.service.SysUserService;
import com.jiangfendou.loladmin.util.RedisUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private static final String USER_MENU = "UserMenu:";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public MenuAuthorityResponse getMenuNav(Long userId) throws BusinessException {
        MenuAuthorityResponse menuAuthorityResponse = new MenuAuthorityResponse();
        List<MenuAuthorityResponse.Menu> menus;
        if (redisUtil.hasKey(USER_MENU + userId)) {
            menus = (List<MenuAuthorityResponse.Menu>)redisUtil.get(USER_MENU + userId);
            log.info("redis获取menu信息 -------{}, menus = {}", USER_MENU + userId, menus);
        } else {
            SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("id", userId)
                .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
            if (sysUser == null) {
                log.info("没有找到的指定用户：userId = {}", userId);
                throw new BusinessException(HttpStatus.NOT_FOUND,
                    new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
            }
            // 获取用户权限信息
            String userAuthorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());
            if (StringUtils.isBlank(userAuthorityInfo)) {
                menuAuthorityResponse.setAuthorities(new ArrayList<>());
            } else {
                String[] userAuthorityArray = userAuthorityInfo.split(",");
                List<String> userAuthorityList = Arrays.asList(userAuthorityArray);
                menuAuthorityResponse.setAuthorities(userAuthorityList);
            }

            List<Long> navMenuIds = sysMenuMapper.getNavMenuIds(userId);

            LambdaQueryWrapper<SysMenu> sysMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysMenuLambdaQueryWrapper.in(SysMenu::getId, navMenuIds);
            sysMenuLambdaQueryWrapper.eq(SysMenu::getIsDeleted, DeletedEnum.NOT_DELETED.getValue())
                .eq(SysMenu::getStatus, StatusEnum.NORMAL.getValue());
            List<SysMenu> sysMenus = sysMenuMapper.selectList(sysMenuLambdaQueryWrapper);

            if (navMenuIds.isEmpty() || sysMenus.isEmpty()) {
                log.info("没有找到的指定用户菜单：userId = {}", userId);
                throw new BusinessException(HttpStatus.NOT_FOUND,
                    new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
            }
            // 转树状结构
            List<SysMenu> sysMenuTrees = buildTreeMenu(sysMenus);
            // 实体转DTO
            menus = convert(sysMenuTrees);
            if (menus.size() > 0) {
                redisUtil.set(USER_MENU + userId, menus, 60*60);
            }
        }
        menuAuthorityResponse.setNav(menus);
        return menuAuthorityResponse;
    }

    @Override
    public List<SearchMenusResponse> searchMenus() throws BusinessException {
        List<SysMenu> sysMenus = this.list(new QueryWrapper<SysMenu>().orderByAsc("order_num")
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (sysMenus.isEmpty()) {
            log.info("menu list is empty");
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        List<SysMenu> sysMenusTree = buildTreeMenu(sysMenus);
        return convertMenuList(sysMenusTree);
    }

    @Override
    public GetMenuDetailResponse getMenuDetail(Long menuId) throws BusinessException {
        GetMenuDetailResponse getMenuDetailResponse = new GetMenuDetailResponse();
        SysMenu sysMenu = this.getOne(new QueryWrapper<SysMenu>()
            .eq("id", menuId)
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysMenu)) {
            log.info("getMenuDetail() ---目标数据没有被找到， menuId = {}", menuId);
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        BeanUtils.copyProperties(sysMenu, getMenuDetailResponse);
        return getMenuDetailResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(UpdateMenuRequest updateMenuRequest) throws BusinessException {
        SysMenu sysMenu = this.getOne(new QueryWrapper<SysMenu>()
            .eq("id", updateMenuRequest.getId())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysMenu)) {
            log.info("updateMenu() ---目标数据没有被找到， userId = {}", updateMenuRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }
        SysMenu perms = this.getOne(new QueryWrapper<SysMenu>()
            .eq("perms", updateMenuRequest.getPerms())
            .eq("is_deleted", DeletedEnum.NOT_DELETED).ne("id", updateMenuRequest.getId()));
        if (perms != null) {
            log.info("updateMenu() ---权限编码已存在， Perms = {}", updateMenuRequest.getPerms());
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                new ApiError(ErrorCodeEnum.MENU_PERM_CODE_EXIST.getCode(),
                    String.format(ErrorCodeEnum.MENU_PERM_CODE_EXIST.getMessage(), updateMenuRequest.getPerms())));
        }
        // 修改menu数据
        sysMenu = new SysMenu();
        BeanUtils.copyProperties(updateMenuRequest, sysMenu);
        if (!this.updateById(sysMenu)) {
            log.info("updateMenu() ---目标数据已经被锁定， menuId = {}", updateMenuRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(DeleteMenuRequest deleteMenuRequest) throws BusinessException {
        SysMenu sysMenu = this.getOne(new QueryWrapper<SysMenu>()
            .eq("id", deleteMenuRequest.getId())
            .eq("is_deleted", DeletedEnum.NOT_DELETED.getValue()));
        if (Objects.isNull(sysMenu)) {
            log.info("updateMenu() ---目标数据没有被找到， userId = {}", deleteMenuRequest.getId());
            throw new BusinessException(HttpStatus.NOT_FOUND,
                new ApiError(ErrorCodeEnum.NOT_FOUND.getCode(), ErrorCodeEnum.NOT_FOUND.getMessage()));
        }

        // 如果不是最低级别的菜单需要做校验
        int count = this.count(new QueryWrapper<SysMenu>().eq("parent_id", deleteMenuRequest.getId()));
        if (count > 0) {
            log.info("deleteMenu() ---目标文件存在子节点， menuId = {}", deleteMenuRequest.getId());
            throw new BusinessException(HttpStatus.FORBIDDEN,
                new ApiError(ErrorCodeEnum.EXIST_CHILD_NODES.getCode(), ErrorCodeEnum.EXIST_CHILD_NODES.getMessage()));
        }
        sysMenu = new SysMenu();
        sysMenu.setId(deleteMenuRequest.getId());
        sysMenu.setDeleteDatetime(LocalDateTime.now());
        sysMenu.setIsDeleted(DeletedEnum.DELETED.getValue());
        sysMenu.setLockVersion(deleteMenuRequest.getLockVersion());
        if (!this.updateById(sysMenu)) {
            log.info("updateMenu() ---目标数据已经被锁定， menuId = {}", deleteMenuRequest.getId());
            throw new BusinessException(HttpStatus.LOCKED,
                new ApiError(ErrorCodeEnum.LOCKED.getCode(), ErrorCodeEnum.LOCKED.getMessage()));
        }

        // 删除关联表的数据
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setIsDeleted(DeletedEnum.DELETED.getValue());
        sysRoleMenuMapper.update(sysRoleMenu, new QueryWrapper<SysRoleMenu>()
            .eq("menu_id", deleteMenuRequest.getId()));
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(SaveMenuRequest saveMenuRequest) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(saveMenuRequest, sysMenu);
        this.save(sysMenu);
    }


    /**
     * menu菜单转树状结构
     * */
    private List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuTrees) {
        List<SysMenu> finalMenus = new ArrayList<>();
        sysMenuTrees.forEach(menu -> {
            sysMenuTrees.forEach(menuChild -> {
                if (Objects.equals(menu.getId(), menuChild.getParentId())) {
                    menu.getChildren().add(menuChild);
                }
            });
            if (Objects.equals(menu.getParentId(), 0L)) {
                System.out.println("menu.getId()" + menu.getId());
                finalMenus.add(menu);
            }
        });
        return finalMenus;
    }

    /**
     * menu实体转换成response实体
     * */
    private List<MenuAuthorityResponse.Menu> convert(List<SysMenu> sysMenuTrees) {
        List<MenuAuthorityResponse.Menu> menuList = new ArrayList<>();
        sysMenuTrees.forEach(sysMenu -> {
            MenuAuthorityResponse.Menu menuResource = new MenuAuthorityResponse.Menu();
            menuResource.setId(sysMenu.getId());
            menuResource.setName(sysMenu.getPerms());
            menuResource.setPath(sysMenu.getPath());
            menuResource.setIcon(sysMenu.getIcon());
            menuResource.setComponent(sysMenu.getComponent());
            menuResource.setTitle(sysMenu.getName());
            if (sysMenu.getChildren().size() > 0) {
                menuResource.setChildren(convert(sysMenu.getChildren()));
            }
            menuList.add(menuResource);
        });

        return menuList;
    }

    private List<SearchMenusResponse> convertMenuList(List<SysMenu> sysMenuTrees) {
        List<SearchMenusResponse> menuList = new ArrayList<>();
        sysMenuTrees.forEach(sysMenu -> {
            SearchMenusResponse menuResource = new SearchMenusResponse();
            BeanUtils.copyProperties(sysMenu, menuResource);
            if (sysMenu.getChildren().size() > 0) {
                menuResource.setChildren(convertMenuList(sysMenu.getChildren()));
            }
            menuList.add(menuResource);
        });
        return menuList;
    }
}
