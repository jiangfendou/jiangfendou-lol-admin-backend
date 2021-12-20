package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * SaveUserRequest.
 * @author jiangmh
 */
@Data
public class UpdateUserRequest {

    @NotNull(message = "id: 不能为空")
    private Long id;

    @NotBlank(message = "username: 不能为空")
    @Size(min = 1, max = 64, message = "username: 最小长度为1，最大长度为64")
    private String username;

    @NotBlank(message = "phone: 不能为空")
    @Size(min = 1, max = 64, message = "phone: 最小长度为1，最大长度为25")
    private String phone;

    @Email(message = "email:格式不正确")
    private String email;

    /**
     * 所在城市
     */
    @Size(min = 1, max = 64, message = "city: 最小长度为1，最大长度为64")
    private String city;

    @NotNull(message = "status: 不能为空")
    private Integer status;

    @NotNull(message = "lockVersion: 不能为空")
    private Integer lockVersion;
}
