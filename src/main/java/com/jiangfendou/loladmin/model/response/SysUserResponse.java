package com.jiangfendou.loladmin.model.response;

import com.jiangfendou.loladmin.entity.SysUser;
import lombok.Data;

/**
 * SysUserResponse.
 * @author jiangmh
 */
@Data
public class SysUserResponse {

    private Long id;

    private String username;

    private String avatar;

    public static SysUserResponse convert(SysUser sysUser) {
        SysUserResponse sysUserResponse = new SysUserResponse();
        sysUserResponse.setId(sysUser.getId());
        sysUserResponse.setUsername(sysUser.getUsername());
        sysUserResponse.setAvatar(sysUser.getAvatar());
        return sysUserResponse;
    }

}
