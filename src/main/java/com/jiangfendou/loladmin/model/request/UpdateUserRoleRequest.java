package com.jiangfendou.loladmin.model.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author jiangmh
 */
@Data
public class UpdateUserRoleRequest {

    @NotNull(message = "userId: 不能为空")
    private Long userId;

    private List<Long> roleIds;
}
