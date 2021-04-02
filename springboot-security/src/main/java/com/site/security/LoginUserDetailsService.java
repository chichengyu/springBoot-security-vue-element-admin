package com.site.security;

import com.google.common.base.Strings;
import com.site.cache.CacheService;
import com.site.common.constant.Constant;
import com.site.common.enums.ResponseCode;
import com.site.common.exception.Assert;
import com.site.pojo.SysPermission;
import com.site.pojo.SysUser;
import com.site.service.IPermissionService;
import com.site.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("loginUserDetailsService")
public class LoginUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;
    @Autowired
    private IPermissionService permissionService;
    @Autowired
    private CacheService cacheService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userKey = this.getEntryCacheKey(username);
        String permissionKey = this.getPermissionCacheKey(username);

        // 1.先查询缓存，没有再查询数据库是否存在该用户
        SysUser user = cacheService.getEntityCache(userKey, Constant.User.USER_KEY_EXPIRE, SysUser.class, () -> userService.getUserByUserName(username));
        Assert.assertNotNull(user,new UsernameNotFoundException(ResponseCode.SYSTEM_USERNAME_NOT_EXISTS.getMessage()));
        // 2.先查询缓存，没有在查询数据库，查询用户所有权限
        List<SysPermission> permissionList =  cacheService.getListCache(permissionKey, Constant.User.USER_KEY_EXPIRE, SysPermission.class, () -> permissionService.permissionListByUserId(user.getId()));
        return this.handleUserDetails(user,permissionList);
    }

    /**
     * 设置权限数据
     * @param user
     * @param permissionList
     * @return
     */
    public UserDetails handleUserDetails(SysUser user,List<SysPermission> permissionList){
        // 将权限过滤后的权限菜单结构数据设置到用户信息，用于在成功处理器里返回数据
        user.setMenus(permissionService.permissionTreeListMenuFilter(permissionList));
        // 获取前端按钮权限标识
        List<String> authBtnPerms = permissionList.stream().filter(item -> !Strings.isNullOrEmpty(item.getCode())).map(item -> item.getCode()).collect(Collectors.toList());
        user.setPermissions(authBtnPerms);

        // 3.将权限给security
        List<String> permsList = permissionList.stream().filter(item -> !Strings.isNullOrEmpty(item.getPerms())).map(item -> item.getPerms()).collect(Collectors.toList());
        String[] perms = permsList.toArray(new String[permsList.size()]);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(perms);
        user.setAuthorities(authorityList);
        return user;
    }

    public String getEntryCacheKey(String username){
        if (username == null){
            username = "";
        }
        return Constant.User.USER_KEY + username;
    }

    public String getPermissionCacheKey(String username){
        if (username == null){
            username = "";
        }
        return Constant.User.PERMISSION_KEY + username;
    }
}
