package com.site.service.impl;

import com.google.common.collect.Sets;
import com.site.common.R;
import com.site.common.enums.ResponseCode;
import com.site.common.exception.BusinessException;
import com.site.common.base.impl.BaseServiceImpl;
import com.site.pojo.SysRole;
import com.site.service.IRolePermissionService;
import com.site.service.IRoleService;
import com.site.util.IdWorker;
import com.site.vo.req.RolePermissionOperationReqVo;
import com.site.vo.req.RoleReqVo;
import com.site.vo.req.RoleUpdateReqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service("roleService")
public class RoleServiceImpl extends BaseServiceImpl<SysRole> implements IRoleService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private IRolePermissionService rolePermissionService;

    /**
     * 新增角色
     * @param roleReqVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> createRole(RoleReqVo roleReqVo) {
        if (null == roleReqVo.getPermissionsIds() || roleReqVo.getPermissionsIds().isEmpty()){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        String roleId = String.valueOf(idWorker.nextId());
        // 角色入库
        SysRole sysRole = SysRole.builder()
                .id(roleId)
                .name(roleReqVo.getName())
                .status(roleReqVo.getStatus())
                .description(roleReqVo.getDescription())
                .createTime(new Date())
                .build();
        int addCount = this.insert(sysRole);
        if (addCount != 1){
            throw new BusinessException(ResponseCode.ERROR);
        }
        // 添加 角色 权限关联表 数据(添加之前，先删除原来角色 id 对应的所有权限id)
        RolePermissionOperationReqVo rolePermissionOperationReqVo = new RolePermissionOperationReqVo();
        rolePermissionOperationReqVo.setRoleId(roleId);
        rolePermissionOperationReqVo.setPermissionIds(roleReqVo.getPermissionsIds());
        rolePermissionService.addRolePermission(rolePermissionOperationReqVo);
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 获取角色详情数据
     * @param roleId
     * @return
     */
    @Override
    public R<Set<String>> getRoleById(String roleId) {
        if (StringUtils.isBlank(roleId)){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        SysRole sysRole = this.findOne(roleId);
        if (sysRole == null){
            log.info("传入 的 id:{}不合法",roleId);
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        // 通过角色id查询拥有的权限ids
        List<String> permissionIdsByRoleId = rolePermissionService.getPermissionIdsByRoleId(roleId);
        Set<String> permissionIds = Sets.newHashSet(permissionIdsByRoleId);
        return R.ok(permissionIds);
    }

    /**
     * 更新角色
     * @param roleUpdateReqVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> updateRole(RoleUpdateReqVo roleUpdateReqVo) {
        SysRole sysRole = this.findOne(roleUpdateReqVo.getId());
        if (sysRole == null){
            throw new BusinessException(ResponseCode.DATA_ERROR);
        }
        BeanUtils.copyProperties(roleUpdateReqVo,sysRole);
        sysRole.setUpdateTime(new Date());
        int updateCount = this.update(sysRole);
        if (updateCount != 1){
            throw new BusinessException(ResponseCode.ERROR);
        }
        // 更新 角色 权限 关联表(更新之前，先删除原来角色 id 对应的所有权限id)
        RolePermissionOperationReqVo rolePermissionOperationReqVo = new RolePermissionOperationReqVo();
        rolePermissionOperationReqVo.setRoleId(sysRole.getId());
        rolePermissionOperationReqVo.setPermissionIds(roleUpdateReqVo.getPermissionsIds());
        rolePermissionService.addRolePermission(rolePermissionOperationReqVo);
        // 标记关联用户主动去刷新
        // 通过 角色id 查询所有的用户ids
        /*List<String> userIdsByRoleId = userRoleService.getUserIdsByRoleId(roleUpdateReqVo.getId());
        if (null != userIdsByRoleId && !userIdsByRoleId.isEmpty()){
            for (String userId : userIdsByRoleId){
                *//**
                 * 标记用户需要重新登录,禁止再访问我们的系统资源
                 *//*
                redisService.set(Constant.JWT_USER_LOGIN_BLACKLIST+userId,userId);
                *//**
                 * 清楚用户授权数据缓存
                 *//*
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
            }
        }*/
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 更新角色状态
     * @param roleId
     * @param status
     * @return
     */
    @Override
    public R<String> updateRoleStatus(String roleId, Integer status) {
        if (StringUtils.isBlank(roleId)|| ObjectUtils.isEmpty(status)){
            return R.error(ResponseCode.DATA_ERROR.getMessage());
        }
        SysRole sysRole = new SysRole();
        sysRole.setId(roleId);
        sysRole.setStatus(status);
        int result = this.update(sysRole);
        if (result == 0){
            throw new BusinessException(ResponseCode.ERROR);
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 删除角色信息
     * @param roleId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> deletedRole(String roleId) {
        // 1.
        SysRole sysRole = new SysRole();
        sysRole.setId(roleId);
        sysRole.setDeleted(0);
        sysRole.setUpdateTime(new Date());
        int updateCount = this.update(sysRole);
        if (updateCount != 1){
            throw new BusinessException(ResponseCode.ERROR);
        }
        // 2.角色菜单权限关联数据删除，通过角色id删除权限id
        rolePermissionService.removeByRoleId(roleId);
        // 3.查询需要标记主动刷新的用户
        /*List<String> userIdsByRoleId = userRoleService.getUserIdsByRoleId(roleId);
        // 4.用户角色关联信息删除，通过角色id删除用户id
        userRoleService.removeUseIdsrRoleId(roleId);
        // 5.把跟该角色关联的用户标记起来，需要主动刷新token
        if (userIdsByRoleId != null && !userIdsByRoleId.isEmpty()){
            for (String userId : userIdsByRoleId){
                *//**
                 * 标记用户需要重新登录,禁止再访问我们的系统资源
                 *//*
                redisService.set(Constant.JWT_USER_LOGIN_BLACKLIST+userId,userId);
                *//**
                 * 清楚用户授权数据缓存
                 *//*
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
            }
        }*/
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }
}
