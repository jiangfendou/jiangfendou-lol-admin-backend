package com.jiangfendou.loladmin.model.request;

import lombok.Data;

/**
 * SearchUserRequest
 * @author jiangmh
 */
@Data
public class SearchUserRequest extends BaseRequest {

    private String username;
}
