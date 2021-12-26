package com.jiangfendou.loladmin.model.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * UpdateRoleMenuRequest.
 * @author jiangmh
 */
@Data
public class UpdateRoleMenuRequest {

    @NotNull(message = "roleId: 不能为空")
    private Long roleId;

    private List<Long> menuIds;
}
