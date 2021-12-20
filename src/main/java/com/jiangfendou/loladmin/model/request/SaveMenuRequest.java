package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;


/**
 * AddMenuRequest.
 * @author jiangmh
 */
@Data
public class SaveMenuRequest {

    /**
     * 父菜单ID，以及菜单为0
     */
    @NotNull(message = "parentId: 不能为空")
    private Long parentId;

    @NotBlank(message = "name: 不能为空")
    @Size(min = 1, max = 64, message = "name: 最小长度为1，最大长度为64")
    private String name;

    /**
     * 菜单URL
     */
    @Size(max = 50)
    private String path;

    /**
     * 授权（多个用逗号隔开，如：user:list,user:create）
     */
    @NotBlank(message = "perms: 不能为空")
    @Size(min = 1, max = 255)
    private String perms;

    @Size(max = 255, message = "component: 最大长度为255")
    private String component;

    /**
     * 类型： 0：目录  1：菜单  2、按钮
     */
    @NotNull(message = "type: 不能为空")
    private Integer type;

    /**
     * 菜单图标
     */
    @Size(max = 32, message = "icon: 最大长度为32")
    private String icon;

    /**
     * 排序
     */
    @NotNull(message = "orderNum: 不能为空")
    private Integer orderNum;

    @NotNull(message = "status: 不能为空")
    private Integer status;
}
