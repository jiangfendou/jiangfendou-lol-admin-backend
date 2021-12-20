package com.jiangfendou.loladmin.controller;


import com.jiangfendou.loladmin.common.ApiResponse;
import com.jiangfendou.loladmin.common.BusinessException;
import com.jiangfendou.loladmin.model.request.SaveMenuRequest;
import com.jiangfendou.loladmin.model.request.DeleteMenuRequest;
import com.jiangfendou.loladmin.model.request.UpdateMenuRequest;
import com.jiangfendou.loladmin.model.response.GetMenuDetailResponse;
import com.jiangfendou.loladmin.model.response.MenuAuthorityResponse;
import com.jiangfendou.loladmin.model.response.SearchMenusResponse;
import com.jiangfendou.loladmin.service.SysMenuService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jiangfendou
 * @since 2021-11-07
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/nav")
    public ApiResponse<MenuAuthorityResponse> getMenuNav(Long userId) throws BusinessException {
        MenuAuthorityResponse menuNav = sysMenuService.getMenuNav(userId);
        return ApiResponse.success(menuNav);
    }

    @GetMapping("/list")
    public ApiResponse<List<SearchMenusResponse>> searchMenus() throws BusinessException {
        return ApiResponse.success(sysMenuService.searchMenus());
    }

    @GetMapping("/detail")
    public ApiResponse<GetMenuDetailResponse> getMenuDetail(Long menuId) throws BusinessException {
        return ApiResponse.success(sysMenuService.getMenuDetail(menuId));
    }

    @PostMapping("/update")
    public ApiResponse updateMenu(@RequestBody @Validated UpdateMenuRequest updateMenuRequest) throws BusinessException {
        sysMenuService.updateMenu(updateMenuRequest);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete")
    public ApiResponse deleteMenu(@RequestBody @Validated DeleteMenuRequest deleteMenuRequest) throws BusinessException {
        sysMenuService.deleteMenu(deleteMenuRequest);
        return ApiResponse.success();
    }

    @PostMapping("/save")
    public ApiResponse saveMenu(@RequestBody @Validated SaveMenuRequest saveMenuRequest) {
        sysMenuService.saveMenu(saveMenuRequest);
        return ApiResponse.success();
    }
}
