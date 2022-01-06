package com.jiangfendou.loladmin.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * SysUserIndexResponse.
 * @author jiangmh
 */
@Data
public class SysUserIndexResponse {

    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLogin;

    private List<String> roleNames;

}
