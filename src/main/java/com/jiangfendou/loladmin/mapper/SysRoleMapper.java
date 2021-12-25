package com.jiangfendou.loladmin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangfendou.loladmin.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangfendou.loladmin.model.request.SearchRoleRequest;
import com.jiangfendou.loladmin.model.response.GetRoleDetailResponse;
import com.jiangfendou.loladmin.model.response.RoleResponse;
import com.jiangfendou.loladmin.model.response.SearchRoleResponse;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2021-11-07
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * searchRoleList()
     * @param userId userId
     * @return List<RoleResponse> List<RoleResponse>
     * */
    List<RoleResponse> searchRoleList(@Param("userId") Long userId);

    /**
     * searchRole()
     * @param searchRoleRequest searchRoleRequest
     * @param page page
     * @return List<SearchRoleResponse> List<SearchRoleResponse>
     * */
    IPage<SearchRoleResponse> searchRole(IPage<SearchRoleResponse> page,
                                         @Param("searchRoleRequest") SearchRoleRequest searchRoleRequest);

}
