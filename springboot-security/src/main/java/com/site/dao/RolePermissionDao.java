package com.site.dao;

import com.site.common.base.IBaseDao;
import com.site.pojo.SysRolePermission;

import java.util.List;

public interface RolePermissionDao extends IBaseDao<SysRolePermission> {

    /**
     * 根据角色id查询拥有的权限id
     * @param roleId
     * @return
     */
    List<String> getPermissionIdsByRoleId(String roleId);

    /**
     * 根据角色id删除多条角色 id 和菜单权限 id 关联数据
     */
    int removeByRoleId(String roleId);

    /**
     * 根据权限id删除相关角色和该菜单权限的关联表信息
     * @param permissionId
     * @return
     */
    int removeByPermissionId(String permissionId);
}
