package com.site.service.impl;

import com.site.common.enums.ResponseCode;
import com.site.common.exception.BusinessException;
import com.site.common.base.impl.BaseServiceImpl;
import com.site.dao.RolePermissionDao;
import com.site.pojo.SysRolePermission;
import com.site.service.IRolePermissionService;
import com.site.util.IdWorker;
import com.site.vo.req.RolePermissionOperationReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("rolePermissionService")
public class RolePermissionServiceImpl extends BaseServiceImpl<SysRolePermission> implements IRolePermissionService {

    @Autowired
    private RolePermissionDao rolePermissionDao;
    @Autowired
    private IdWorker idWorker;

    /**
     * 插入角色权限关联表数据
     * @param operationReqVo
     */
    @Override
    public void addRolePermission(RolePermissionOperationReqVo operationReqVo) {
        if (null == operationReqVo.getPermissionIds()){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        // 添加之前，先删除原来角色 id 对应的所有权限id
        rolePermissionDao.removeByRoleId(operationReqVo.getRoleId());
        // 权限id集合为空，表示删除该角色对应的所有权限
        if (operationReqVo.getPermissionIds().isEmpty()){
            return;
        }
        Date date = new Date();
        List<SysRolePermission> list = new ArrayList<>();
        for (String permissionId : operationReqVo.getPermissionIds()){
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setId(String.valueOf(idWorker.nextId()));
            sysRolePermission.setRoleId(operationReqVo.getRoleId());
            sysRolePermission.setPermissionId(permissionId);
            sysRolePermission.setCreateTime(date);
            list.add(sysRolePermission);
        }
        int result = rolePermissionDao.insertList(list);
        if (result == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
    }

    /**
     * 根据角色id查询拥有的权限id
     * @param roleId
     * @return
     */
    @Override
    public List<String> getPermissionIdsByRoleId(String roleId) {
        return rolePermissionDao.getPermissionIdsByRoleId(roleId);
    }

    /**
     * 通过角色id删除权限id
     * @param roleId
     * @return
     */
    @Override
    public int removeByRoleId(String roleId) {
        return rolePermissionDao.removeByRoleId(roleId);
    }
}
