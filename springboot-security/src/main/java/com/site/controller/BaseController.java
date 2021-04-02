package com.site.controller;

import com.site.common.constant.Constant;
import com.site.pojo.SysUser;
import com.site.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    /**
     * 从 accessToken 中获取 token
     * @param token
     * @return
     */
    protected String getUserId(String token){
        String userId = null;// 操作人
        Claims claims = JwtTokenUtil.parseToken(token);
        if (claims != null){
            userId = (String) claims.get(Constant.User.JWT_USER_ID);
        }
        return userId;
    }

    /**
     * 获取登录账户username
     * @return
     */
    protected String getUsername(String token){
        return JwtTokenUtil.getSubject(token);
    }

    /**
     * 获取登录账户username
     * @return
     */
    protected String getUsername(){
        return getLoginUser().getUsername();
    }

    /**
     * 获取当前登录用户，也可以获取token中的用户
     * @return
     */
    protected SysUser getLoginUser(){
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
