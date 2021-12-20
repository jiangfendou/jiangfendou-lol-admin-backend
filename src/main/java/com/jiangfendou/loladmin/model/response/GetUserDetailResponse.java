package com.jiangfendou.loladmin.model.response;

import lombok.Data;

/**
 * GetUserDetailResponse.
 * @author jiangmh
 */
@Data
public class GetUserDetailResponse {

    private Long id;

    private String username;

    private String password;

    private String phone;

    private String email;

    private String city;

    private Integer status;

    private Integer lockVersion;
}
