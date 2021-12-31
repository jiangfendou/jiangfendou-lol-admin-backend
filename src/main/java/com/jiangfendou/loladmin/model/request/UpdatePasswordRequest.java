package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * UpdatePasswordRequest.
 * @author jiangmh
 */
@Data
public class UpdatePasswordRequest {

    @NotNull(message = "id: 不能为空")
    private Long id;

    @NotBlank(message = "currentPass: 不能为空")
    private String currentPass;

    @NotBlank(message = "password: 不能为空")
    private String password;

    @NotBlank(message = "checkPass: 不能为空")
    private String checkPass;

    @NotNull(message = "lockVersion: 不能为空")
    private Integer lockVersion;

}
