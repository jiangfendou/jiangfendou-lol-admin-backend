package com.jiangfendou.loladmin.model.request;

import lombok.Data;

@Data
public class SearchRoleRequest extends BaseRequest{

    private String name;

    private String code;
}
