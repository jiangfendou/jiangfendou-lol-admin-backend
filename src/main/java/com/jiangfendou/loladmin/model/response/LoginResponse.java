package com.jiangfendou.loladmin.model.response;

import lombok.Data;

/**
 * LoginResponse.
 * @author jiangmh
 */
@Data
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private String username;

    private Long userId;
}
