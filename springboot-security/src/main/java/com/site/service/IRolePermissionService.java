package com.site.service;

import com.site.common.base.IBaseService;
import com.site.pojo.SysRolePermission;
import com.site.vo.req.RolePermissionOperationReqVo;

import java.util.List;

public interface IRolePermissionService extends IBaseService<SysRolePermission> {

    /**
     * 插入角色权限关联表数据
     * @param operationReqVo
     */
    void addRolePermission(RolePermissionOperationReqVo operationReqVo);

    /**
     * 根据角色id查询拥有的权限id
     * @param roleId
     * @return
     */
    List<String> getPermissionIdsByRoleId(String roleId);

    /**
     * 通过角色id删除权限id
     * @param roleId
     * @return
     */
    int removeByRoleId(String roleId);
}
