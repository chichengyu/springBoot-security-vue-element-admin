package com.site.service;

import com.site.common.R;
import com.site.common.base.IBaseService;
import com.site.pojo.SysPermission;
import com.site.vo.req.PermissionAddReqVo;
import com.site.vo.req.PermissionUpdateReqVo;
import com.site.vo.resp.PermissionRespNodeTreeVo;
import com.site.vo.resp.PermissionRespNodeVo;

import java.util.List;

public interface IPermissionService extends IBaseService<SysPermission> {

    /**
     * 获取所有权限（包括按钮），角色添加/编辑/分配权限时用到的结结构数据
     * @return
     */
    public List<PermissionRespNodeVo> selectAllTree();

    /**
     * 查询所有权限列表（树形表格结果数据展示），表格列表时展示
     * 树形表格结果数据组装
     * @return
     */
    public List<SysPermission> selectAll();

    /**
     * 根据用户id查询用户拥有的权限列表，不包括菜单结构，用于 security认证授权
     * @param userId
     * @return
     */
    List<SysPermission> permissionListByUserId(String userId);

    /**
     * 筛选用户拥有的权限列表，包括菜单结构，用于返回用户菜单
     * @param permissionList
     * @return
     */
    List<PermissionRespNodeVo> permissionTreeListMenuFilter(List<SysPermission> permissionList);

    /**
     * 添加权限的上级选择权限树结构展示（递归），不需要展示到按钮
     * @return
     */
    public List<PermissionRespNodeTreeVo> selectAllMenuByTree();

    /**
     * 新增权限
     * @param permissionAddReqVo
     * @return
     */
    R<String> addPermission(PermissionAddReqVo permissionAddReqVo);

    /**
     * 编辑权限
     * @param permissionUpdateReqVo
     * @return
     */
    R<String> updatePermission(PermissionUpdateReqVo permissionUpdateReqVo);

    /**
     * 删除权限
     * @param permissionId
     * @return
     */
    R<String> deletedPermission(String permissionId);
}
