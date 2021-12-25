package com.jiangfendou.loladmin.mapper;

import com.jiangfendou.loladmin.entity.SysRoleMenu;
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
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * deleteRoleMenuBatch()
     * @param deleteRoleIds deleteRoleIds
     * */
    void deleteRoleMenuBatch(@Param("deleteRoleIds") List<Long> deleteRoleIds);

}
