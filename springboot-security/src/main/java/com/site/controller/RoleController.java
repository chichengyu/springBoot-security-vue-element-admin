package com.site.controller;

import com.site.common.PageResult;
import com.site.common.R;
import com.site.common.annotation.Log;
import com.site.pojo.SysRole;
import com.site.service.IRoleService;
import com.site.vo.req.RolePageReqVo;
import com.site.vo.req.RoleReqVo;
import com.site.vo.req.RoleUpdateReqVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import java.util.Set;

@Api(value = "组织管理-角色管理",tags = "角色管理相关接口")
@RequestMapping("/api")
@RestController
public class RoleController extends BaseController{

    @Autowired
    private IRoleService roleService;

    @ApiOperation(value = "分页获取角色数据接口",notes = "分页获取角色数据接口")
    @PreAuthorize("hasAuthority('sys:role:list')")
    @Log(title = "组织管理-角色管理",action = "分页获取角色数据接口")
    @PostMapping("/roles")
    public R<PageResult> pageInfo(@RequestBody RolePageReqVo rolePageReqVo){
        SysRole sysRole = SysRole.builder().deleted(1).build();
        return R.ok(roleService.findPage(rolePageReqVo.getPageNum(),rolePageReqVo.getPageSize(),sysRole));
    }

    @ApiOperation(value = "新增角色接口",notes = "新增角色")
    @PreAuthorize("hasAuthority('sys:role:add')")
    @Log(title = "组织管理-角色管理",action = "新增角色接口")
    @PostMapping("/role")
    public R<String> createRole(@RequestBody @Valid RoleReqVo roleReqVo){
        return roleService.createRole(roleReqVo);
    }

    @ApiOperation(value = "获取角色详情接口",notes = "获取角色详情接口")
    @PreAuthorize("hasAuthority('sys:role:detail')")
    @Log(title = "组织管理-角色管理",action = "获取角色详情接口")
    @GetMapping("/role/{id}")
    public R<Set<String>> getRoleBy(@PathVariable("id") String roleId){
        return roleService.getRoleById(roleId);
    }

    @ApiOperation(value = "更新角色信息",notes = "更新角色信息接口")
    @PreAuthorize("hasAuthority('sys:role:update')")
    @Log(title = "组织管理-角色管理",action = "更新角色信息接口")
    @PutMapping("/role")
    public R<String> updateRole(@RequestBody @Valid RoleUpdateReqVo roleUpdateReqVo){
        return roleService.updateRole(roleUpdateReqVo);
    }

    @ApiOperation(value = "更新角色状态",notes = "更新角色状态接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "status",value = "状态status",required = true,dataType = "Integer",paramType = "form"),
    })
    @PreAuthorize("hasAuthority('sys:role:update')")
    @Log(title = "组织管理-角色管理",action = "更新角色状态接口")
    @PostMapping("/role/{id}/{status}")
    public R<String> updateRoleStatus(@PathVariable("id")String roleId,@PathVariable("status") Integer status){
        return roleService.updateRoleStatus(roleId,status);
    }

    @ApiOperation(value = "删除角色信息",notes = "删除角色接口")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Log(title = "组织管理-角色管理",action = "删除角色接口")
    @DeleteMapping("/role/{id}")
    public R<String> deletedRole(@PathVariable("id") String roleId){
        return roleService.deletedRole(roleId);
    }
}
