package com.jiangfendou.loladmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID，以及菜单为0
     */
    private Long parentId;

    private String name;

    /**
     * 菜单URL
     */
    private String path;

    /**
     * 授权（多个用逗号隔开，如：user:list,user:create）
     */
    private String perms;

    private String component;

    /**
     * 类型： 0：目录  1：菜单  2、按钮
     */
    private Integer type;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer orderNum;

    @TableField(fill = FieldFill.UPDATE)
    @Version
    private Integer lockVersion;

    private Integer status;

    private LocalDateTime deleteDatetime;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();

}
