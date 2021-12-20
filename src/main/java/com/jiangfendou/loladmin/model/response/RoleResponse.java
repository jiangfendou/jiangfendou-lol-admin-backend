package com.jiangfendou.loladmin.model.response;

import lombok.Data;

/**
 * RoleResponse.
 * @author jiangmh
 */
@Data
public class RoleResponse {

    private String id;

    private String name;

    private String code;

    /**
     * 授权（多个用逗号隔开，如：user:list,user:create）
     */
    private String remark;

    private Integer lockVersion;
}
