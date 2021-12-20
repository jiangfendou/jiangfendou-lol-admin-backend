package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * RepassUserRequest.
 * @author jiangmh
 */
@Data
public class RepassUserRequest {

    @NotNull(message = "id:不能为空")
    private Long id;

    @NotNull(message = "lockVersion:不能为空")
    private Integer lockVersion;

    private String password;

}
