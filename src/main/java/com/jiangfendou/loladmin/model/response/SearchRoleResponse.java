package com.jiangfendou.loladmin.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * SearchRoleResponse.
 * @author jiangmh
 */
@Data
public class SearchRoleResponse {

    private Long id;

    private String name;

    private String code;

    private String remark;

    private Integer status;

    private Integer lockVersion;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDatetime;

    private List<MenuResponse> menuIds;

    @Data
    public static class MenuResponse{
        private Long id;
    }
}
