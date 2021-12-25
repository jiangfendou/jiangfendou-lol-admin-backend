package com.jiangfendou.loladmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
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
public class SysRole extends BaseEntity {

    private String name;

    private String code;

    /**
     * 授权（多个用逗号隔开，如：user:list,user:create）
     */
    private String remark;

    @TableField(fill = FieldFill.UPDATE)
    @Version
    private Integer lockVersion;

    private Integer status;


}
