package com.jiangfendou.loladmin.model.request;

import cn.hutool.crypto.digest.mac.MacEngine;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * DeleteUserBatchRequest.
 * @author jiangmh
 */
@Data
public class DeleteUserBatchRequest {

    @NotNull(message = "deleteUserBatch:不能为空")
    private List<DeleteUser> deleteUserBatch;

   @Data
    public static class DeleteUser {

       @NotNull(message = "userId:不能为空")
       private Long userId;

       @NotNull(message = "lockVersion:不能为空")
       private Integer lockVersion;
    }
}
