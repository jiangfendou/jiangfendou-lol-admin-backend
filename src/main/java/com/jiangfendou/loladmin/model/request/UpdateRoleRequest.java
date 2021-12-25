package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * SaveRoleRequest.
 * @author jiangmh
 */
@Data
public class UpdateRoleRequest {

    @NotNull(message = "id: 不能为空")
    private Long id;

    @NotBlank(message = "name: 不能为空")
    private String name;

    @NotBlank(message = "code: 不能为空")
    private String code;

    @NotNull(message = "status: 不能为空")
    private Integer status;

    /**
     * 授权（多个用逗号隔开，如：user:list,user:create）
     */
    private String remark;

    @NotNull(message = "lockVersion: 不能为空")
    private Integer lockVersion;
}
