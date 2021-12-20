package com.jiangfendou.loladmin.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * SearchUserResponse.
 * @author jiangmh
 */
@Data
public class SearchUserResponse {

    private Long id;

    private String username;

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

    private Integer lockVersion;

    private Integer status;

    private List<RoleResponse> roles;

    @Data
    public static class RoleResponse {

        private Long id;

        private String name;
    }
}
