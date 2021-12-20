package com.jiangfendou.loladmin.service;

import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.loladmin.model.request.SaveMenuRequest;
import com.jiangfendou.loladmin.model.request.DeleteMenuRequest;
import com.jiangfendou.loladmin.model.request.UpdateMenuRequest;
import com.jiangfendou.loladmin.model.response.GetMenuDetailResponse;
import com.jiangfendou.loladmin.model.response.MenuAuthorityResponse;
import com.jiangfendou.loladmin.model.response.SearchMenusResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * getMenuNav()
     * @param userId userId
     * @return MenuAuthorityResponse MenuAuthorityResponse
     * @throws BusinessException BusinessException
     * */
    MenuAuthorityResponse getMenuNav(Long userId) throws BusinessException;

    /**
     * searchMenus()
     * @return List<SearchMenusResponse> List<SearchMenusResponse>
     * @throws BusinessException BusinessException
     * */
    List<SearchMenusResponse> searchMenus() throws BusinessException;

    /**
     * getMenuDetail()
     * @param menuId menuId
     * @return List<GetMenuDetailResponse> List<GetMenuDetailResponse>
     * @throws BusinessException BusinessException
     * */
    GetMenuDetailResponse getMenuDetail(Long menuId) throws BusinessException;

    /**
     * updateMenu()
     * @param updateMenuRequest updateMenuRequest
     * @throws  BusinessException BusinessException
     * */
    void updateMenu(UpdateMenuRequest updateMenuRequest) throws BusinessException;

    /**
     * deleteMenu()
     * @param deleteMenuRequest deleteMenuRequest
     * @throws  BusinessException BusinessException
     * */
    void deleteMenu(DeleteMenuRequest deleteMenuRequest) throws BusinessException;

    /**
     * addMenu()
     * @param addMenuRequest addMenuRequest
     * */
    void saveMenu(SaveMenuRequest addMenuRequest);

}
