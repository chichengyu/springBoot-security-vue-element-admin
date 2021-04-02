package com.site.service;

import com.site.common.R;
import com.site.common.base.IBaseService;
import com.site.pojo.SysRole;
import com.site.vo.req.RoleReqVo;
import com.site.vo.req.RoleUpdateReqVo;

import java.util.Set;

public interface IRoleService extends IBaseService<SysRole> {

    /**
     * 新增角色
     * @param roleReqVo
     * @return
     */
    R<String> createRole(RoleReqVo roleReqVo);

    /**
     * 获取角色详情数据
     * @param roleId
     * @return
     */
    R<Set<String>> getRoleById(String roleId);

    /**
     * 编辑角色
     * @param roleUpdateReqVo
     * @return
     */
    R<String> updateRole(RoleUpdateReqVo roleUpdateReqVo);

    /**
     * 更新角色状态
     * @param roleId
     * @param status
     * @return
     */
    R<String> updateRoleStatus(String roleId, Integer status);

    /**
     * 删除角色信息
     * @param roleId
     * @return
     */
    R<String> deletedRole(String roleId);
}
