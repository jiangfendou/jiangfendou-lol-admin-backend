package com.jiangfendou.loladmin.mapper;

import com.jiangfendou.loladmin.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * deleteUserRoleBatch()
     * @param deleteUserIds deleteUserIds
     * @return int int
     * */
    int deleteUserRoleBatch(@Param("deleteUserIds") List<Long> deleteUserIds);
}
