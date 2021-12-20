package com.jiangfendou.loladmin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangfendou.loladmin.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangfendou.loladmin.model.request.DeleteUserBatchRequest;
import com.jiangfendou.loladmin.model.request.DeleteUserRequest;
import com.jiangfendou.loladmin.model.request.RepassUserRequest;
import com.jiangfendou.loladmin.model.request.SearchUserRequest;
import com.jiangfendou.loladmin.model.request.UpdateUserRequest;
import com.jiangfendou.loladmin.model.response.SearchUserResponse;
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
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * getNavMenusIds()
     * @param userId userId
     * @return List<Long> List<Long>
     * */
    List<Long> getNavMenusIds(@Param("userId") Long userId);

    /**
     * searchUserList()
     * @param roleId roleId
     * @return List<SysUser> List<SysUser>
     * */
    List<SysUser> searchUserList(@Param("roleId") Long roleId);

    /**
     * searchUser()
     * @param page page
     * @param searchUserRequest searchUserRequest
     * @return IPage<SearchUserResponse> IPage<SearchUserResponse>
     * */
    IPage<SearchUserResponse> searchUser(IPage<SearchUserResponse> page,
                                         @Param("searchUserRequest") SearchUserRequest searchUserRequest);

    /**
     * deleteUser()
     * @param repassUserRequest repassUserRequest
     * @return int int
     * */
    int repassUser(@Param("repassUserRequest") RepassUserRequest repassUserRequest);

}
