package com.jiangfendou.loladmin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import java.time.LocalDateTime;
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
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    /**
     * 头像
     */
    private String avatar;

    private String phone;

    private String email;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLogin;

    @TableField(fill = FieldFill.UPDATE)
    @Version
    private Integer lockVersion;

    private Integer status;

    private LocalDateTime deleteDatetime;

}
