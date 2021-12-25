package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * DeleteRoleRequest.
 * @author jiangmh
 */
@Data
public class DeleteRoleRequest {

    @NotNull(message = "id: 不能为空")
    private Long id;

    @NotNull(message = "lockVersion: 不能为空")
    private Integer lockVersion;
}
