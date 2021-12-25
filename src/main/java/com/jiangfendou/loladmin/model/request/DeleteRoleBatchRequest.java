package com.jiangfendou.loladmin.model.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * DeleteUserBatchRequest.
 * @author jiangmh
 */
@Data
public class DeleteRoleBatchRequest {

    @NotNull(message = "deleteRoleBatch:不能为空")
    private List<DeleteRole> deleteRoleBatch;

   @Data
    public static class DeleteRole {

       @NotNull(message = "roleId:不能为空")
       private Long roleId;

       @NotNull(message = "lockVersion:不能为空")
       private Integer lockVersion;
    }
}
