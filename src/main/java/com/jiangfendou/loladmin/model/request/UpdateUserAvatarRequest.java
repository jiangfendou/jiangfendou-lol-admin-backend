package com.jiangfendou.loladmin.model.request;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * UpdateUserAvatarRequest.
 * @author jiangmh
 */
@Data
public class UpdateUserAvatarRequest {

    @NotNull(message = "id: 不能为空")
    private Long id;

    @NotNull(message = "multipartFile: 不能为空")
    private MultipartFile multipartFile;

    @NotNull(message = "lockVersion: 不能为空")
    private Integer lockVersion;

}
