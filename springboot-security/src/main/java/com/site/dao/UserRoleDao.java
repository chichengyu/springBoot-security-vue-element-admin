package com.site.dao;

import com.site.common.base.IBaseDao;
import com.site.pojo.SysUserRole;

import java.util.List;

public interface UserRoleDao extends IBaseDao<SysUserRole> {

    /**
     * 通过用户id获取该用户所拥有的角色列表
     * @param userId
     * @return
     */
    List<String> getRoleIdsByUserId(String userId);

    /**
     * 根据用户id 删除用户拥有的角色数据
     * @param userId
     * @return
     */
    int removeRoleIdsByUserId(String userId);
}
