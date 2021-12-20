package com.jiangfendou.loladmin.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class GetRoleDetailResponse {

    private Long id;

    private String name;

    private String code;

    private String remark;

    private String status;

    private LocalDateTime created;

    private LocalDateTime updated;

    private Integer lockVersion;

    private List<Long> menuIds;

}
