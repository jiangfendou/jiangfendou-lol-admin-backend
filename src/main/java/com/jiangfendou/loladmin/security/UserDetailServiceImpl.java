package com.jiangfendou.loladmin.security;

import com.jiangfendou.loladmin.entity.SysUser;
import com.jiangfendou.loladmin.enums.ErrorCodeEnum;
import com.jiangfendou.loladmin.service.SysUserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailServiceImpl.
 * @author jiangmh
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) {

        SysUser sysUser = sysUserService.getByUseName(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException(ErrorCodeEnum.ACCOUNT_PASSWORD_ERROR.getMessage());
        }
        return new AccountUser(sysUser.getId(), sysUser.getUsername(),
                    sysUser.getPassword(), getUserAuthority(sysUser.getId()));
    }

    /**
     * 获取用户权限信息（角色，菜单权限）
     * @param userId userId
     * @return List<GrantedAuthority>
     * */
    public List<GrantedAuthority> getUserAuthority(Long userId) {
        // 角色（ROLE_admin), 菜单操作权限 sys:user:list
        // ROLE_admin,sys:user:list .....
        String authority = sysUserService.getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }

}
