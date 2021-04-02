package com.site.controller;

import com.site.common.R;
import com.site.common.annotation.Log;
import com.site.pojo.SysPermission;
import com.site.service.IPermissionService;
import com.site.vo.req.PermissionAddReqVo;
import com.site.vo.req.PermissionUpdateReqVo;
import com.site.vo.resp.PermissionRespNodeTreeVo;
import com.site.vo.resp.PermissionRespNodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(value = "组织管理-菜单权限管理",tags = "菜单权限管理相关接口")
@RestController
@RequestMapping("/api")
public class PermissionController extends BaseController{

    @Autowired
    private IPermissionService permissionService;

    @ApiOperation(value = "获取所有权限（包括按钮）",notes = "角色添加/编辑/分配权限时用到的结果数据")
    @Log(title = "组织管理-菜单权限管理",action = "获取所有的菜单权限数据接口")
    @PreAuthorize("hasAuthority('sys:role:add') OR hasAuthority('sys:role:update')")
    @GetMapping("/permissions/tree/all")
    public R<List<PermissionRespNodeVo>> getAllPermissionTree(){
        return R.ok(permissionService.selectAllTree());
    }

    @ApiOperation(value = "菜单权限表格展示-获取所有的菜单权限数据接口",notes = "菜单权限接口")
    @PreAuthorize("hasAuthority('sys:permission:list')")
    @Log(title = "组织管理-菜单权限管理",action = "权限表格列表数据展示")
    @GetMapping("/permissions")
    public R<List<SysPermission>> getAllPermission(){
        return R.ok(permissionService.selectAll());
    }

    @ApiOperation(value = "菜单权限树",notes = "只递归查询目录与菜单，不要按钮，添加/编辑-权限的上级选择权限树结构展示")
    @Log(title = "组织管理-菜单权限管理",action = "新增菜单权限接口")
    @PreAuthorize("hasAuthority('sys:permission:update') OR hasAuthority('sys:permission:add')")
    @GetMapping("/permissions/tree")
    public R<List<PermissionRespNodeTreeVo>> getAllPermissionTreeExBtn(){
        return R.ok(permissionService.selectAllMenuByTree());
    }

    @ApiOperation(value = "新增权限",notes = "新增菜单权限接口")
    @PreAuthorize("hasAuthority('sys:permission:add')")
    @Log(title = "组织管理-菜单权限管理",action = "新增菜单权限接口")
    @PostMapping("/permission")
    public R<String> createPermission(@RequestBody @Valid PermissionAddReqVo permissionAddReqVO){
        return permissionService.addPermission(permissionAddReqVO);
    }

    @ApiOperation(value = "更新权限",notes = "新更新菜单权限接口")
    @PreAuthorize("hasAuthority('sys:permission:update')")
    @Log(title = "组织管理-菜单权限管理",action = "编辑菜单权限接口")
    @PutMapping("/permission")
    public R<String> updatePermission(@RequestBody @Valid PermissionUpdateReqVo permissionUpdateReqVo){
        return permissionService.updatePermission(permissionUpdateReqVo);
    }

    @ApiOperation(value = "删除权限",notes = "删除菜单权限接口")
    @PreAuthorize("hasAuthority('sys:permission:delete')")
    @Log(title = "组织管理-菜单权限管理",action = "删除菜单权限接口")
    @DeleteMapping("/permission/{permissionId}")
    public R<String> deletedPermission(@PathVariable("permissionId") String permissionId){
        return permissionService.deletedPermission(permissionId);
    }
}
