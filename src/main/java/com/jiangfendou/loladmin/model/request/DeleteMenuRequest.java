package com.jiangfendou.loladmin.model.request;


import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * DeleteMenuRequest.
 * @author jiangmh
 */
@Data
public class DeleteMenuRequest {

    @NotNull(message = "id:不能为空")
    private Long id;

    @NotNull(message = "lockVersion:不能为空")
    private Integer lockVersion;

    private LocalDateTime deleteDatetime;
}
