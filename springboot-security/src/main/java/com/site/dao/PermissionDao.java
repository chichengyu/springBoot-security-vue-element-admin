package com.site.dao;

import com.site.common.base.IBaseDao;
import com.site.pojo.SysPermission;

import java.util.List;

public interface PermissionDao extends IBaseDao<SysPermission> {
    /**
     * 查询用户拥有的权限菜单列表
     * @return
     */
    List<SysPermission> getPermissionByUserId(String userId);

    /**
     * 通过 id 查询是否有子级
     * @param id
     * @return
     */
    int selectChild(String id);
}
