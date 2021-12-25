package com.jiangfendou.loladmin.model.response;

import java.util.List;
import lombok.Data;

/**
 * GetRoleDetailResponse.
 * @author jiangmh
 */
@Data
public class GetRoleDetailResponse {

    private Long id;

    private String name;

    private String code;

    private String remark;

    private Integer status;

    private Integer lockVersion;

}
