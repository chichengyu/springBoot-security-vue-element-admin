package com.site.controller;

import com.google.common.collect.Maps;
import com.site.common.PageResult;
import com.site.common.R;
import com.site.common.annotation.Log;
import com.site.common.constant.Constant;
import com.site.common.enums.ResponseCode;
import com.site.pojo.SysUser;
import com.site.service.IUserService;
import com.site.vo.req.UserAddReqVo;
import com.site.vo.req.UserOwnRoleReqVo;
import com.site.vo.req.UserPageReqVo;
import com.site.vo.req.UserUpdateDetailInfoReqVo;
import com.site.vo.req.UserUpdatePasswordReqVo;
import com.site.vo.req.UserUpdateReqVo;
import com.site.vo.resp.UserOwnRoleRespVo;
import com.site.vo.resp.UserRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(value = "用户登陆/登出",tags = "用户登陆/登出相关的接口")
@RestController
@RequestMapping("/api")
public class UserController extends BaseController{

    // 验证码key
    public static final String CAPTCHA_CODE = "CAPTCHA_CODE";

    @Autowired
    private IUserService userService;

    /**
     * 获取验证码
     * @param request
     * @return
     */
    @GetMapping("/captcha")
    public R<Map<String, String>> getCaptchaCode(HttpServletRequest request){
        Map<String,String> map = Maps.newHashMap();
        String code = RandomStringUtils.randomAlphabetic(4);
        request.getSession().setAttribute(CAPTCHA_CODE,code);
        map.put("code",code);
        return R.ok(map);
    }

    @ApiOperation(value = "分页查询用户",notes = "分页查询用户接口")
    @Log(title = "组织管理-用户管理",action = "分页查询用户接口")
    @PreAuthorize("hasAuthority('sys:user:list')")
    @PostMapping("/users")
    public R<PageResult> pageInfo(@RequestBody @Valid UserPageReqVo userPageReqVo){
        SysUser sysUser = SysUser.builder().deleted(1).build();
        return R.ok(userService.findPage(userPageReqVo.getPageNum(),userPageReqVo.getPageSize(),sysUser));
    }

    @ApiOperation(value = "新增用户",notes = "新增用户接口")
    @PreAuthorize("hasAuthority('sys:user:add')")
    @Log(title = "组织管理-用户管理",action = "新增用户接口")
    @PostMapping("/user")
    public R<String> createUser(@RequestBody @Valid UserAddReqVo userAddReqVo){
        return userService.createUser(userAddReqVo);
    }

    @Log(title = "组织管理-用户管理",action = "查询用户拥有的角色数据接口")
    @ApiOperation(value = "查询用户拥有的角色数据接口",notes = "查询用户拥有的角色数据接口")
    @GetMapping("/user/roles/{userId}")
    public R<UserOwnRoleRespVo> getUserOwnRole(@PathVariable("userId")String userId){
        return userService.getUserOwnRole(userId);
    }

    @ApiOperation(value = "更新用户角色",notes = "保存用户拥有的角色信息接口")
    @PreAuthorize("hasAuthority('sys:user:role:update')")
    @Log(title = "组织管理-用户管理",action = "保存用户拥有的角色信息接口")
    @PutMapping("/user/roles")
    public R<String> saveUserOwnRole(@RequestBody @Valid UserOwnRoleReqVo vo){
        return userService.setUserOwnRole(vo);
    }

    @ApiOperation(value = "重新用户密码",notes = "重新用户密码接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "form")
    @PreAuthorize("hasAuthority('sys:user:role:update')")
    @Log(title = "组织管理-重置用户密码",action = "重置用户密码接口")
    @GetMapping("/user/password/{id}")
    public R<String> resetUpdatePassword(@PathVariable("id") String userId){
        return userService.resetUpdatePassword(userId);
    }

    @ApiOperation(value = "更新用户",notes = "更新用户接口")
    @PreAuthorize("hasAuthority('sys:user:update')")
    @Log(title = "组织管理-用户管理",action = "列表修改用户信息接口")
    @PutMapping("/user")
    public R<String> updateUserInfo(@RequestBody @Valid UserUpdateReqVo userUpdateReqVo, HttpServletRequest request){
        String accessToken = request.getHeader(Constant.User.ACCESS_TOKEN);
        String userId = getUserId(accessToken);// 操作人
        if (userId == null){
            return R.error(ResponseCode.TOKEN_NO_AVAIL.getMessage());
        }
        return userService.updateUserInfo(userUpdateReqVo,userId);
    }

    @ApiOperation(value = "批量/删除用户",notes = "批量/删除用户接口")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Log(title = "组织管理-用户管理",action = "批量/删除用户接口")
    @DeleteMapping("/user")
    public R<String> deletedUsers(@RequestBody @ApiParam(value = "用户id集合") List<String> list, HttpServletRequest request){
        String accessToken = request.getHeader(Constant.User.ACCESS_TOKEN);
        String userId = getUserId(accessToken);// 操作人
        if (userId == null){
            return R.error(ResponseCode.TOKEN_NO_AVAIL.getMessage());
        }
        return userService.deletedUsers(list,userId);
    }

    @ApiOperation(value = "用户信息详情",notes = "用户信息详情接口")
    @Log(title = "组织管理-用户管理",action = "用户信息详情接口")
    @GetMapping("/user/info")
    public R<UserRespVo> detailInfo(HttpServletRequest request){
        String accessToken = request.getHeader(Constant.User.ACCESS_TOKEN);
        String userId = getUserId(accessToken);// 操作人
        if (userId == null){
            return R.error(ResponseCode.TOKEN_NO_AVAIL.getMessage());
        }
        return userService.detailInfo(userId);
    }

    @ApiOperation(value = "保存个人信息",notes = "保存个人信息接口")
    @Log(title = "组织管理-用户管理",action = "保存个人信息接口")
    @PutMapping("/user/info")
    public R<String> saveUserInfo(@RequestBody UserUpdateDetailInfoReqVo updateDetailInfoReqVo, HttpServletRequest request){
        String accessToken = request.getHeader(Constant.User.ACCESS_TOKEN);
        String userId = getUserId(accessToken);// 操作人
        if (userId == null){
            return R.error(ResponseCode.TOKEN_NO_AVAIL.getMessage());
        }
        return userService.userUpdateDetailInfo(updateDetailInfoReqVo,userId);
    }

    @ApiOperation(value = "修改个人密码",notes = "修改个人密码接口")
    @Log(title = "组织管理-用户管理",action = "修改个人密码接口")
    @PutMapping("/user/password")
    public R<String> updatePassword(@RequestBody @Valid UserUpdatePasswordReqVo userUpdatePasswordReqVo, HttpServletRequest request){
        String accessToken = request.getHeader(Constant.User.ACCESS_TOKEN);
        //String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
        String userId = getUserId(accessToken);// 操作人
        if (userId == null){
            return R.error(ResponseCode.TOKEN_NO_AVAIL.getMessage());
        }
        return userService.userUpdatePassword(userUpdatePasswordReqVo,userId);
    }
}
