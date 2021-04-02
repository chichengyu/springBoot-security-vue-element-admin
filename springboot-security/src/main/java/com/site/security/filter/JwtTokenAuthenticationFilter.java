package com.site.security.filter;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.site.common.constant.Constant;
import com.site.common.enums.ResponseCode;
import com.site.common.exception.Assert;
import com.site.pojo.SysPermission;
import com.site.pojo.SysUser;
import com.site.security.JwtAuthenticationToken;
import com.site.security.LoginUserDetailsService;
import com.site.security.SecurityException;
import com.site.util.JwtTokenUtil;
import com.site.util.RedisUtil;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * 权限认证过滤器
 */
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    @Setter
    private String[] openUrl;
    @Setter
    private RedisUtil redisUtil;
    //@Setter
    //private LoginFailureHandler loginFailureHandler;
    @Setter
    private UserLoginFilter userLoginFilter;
    @Setter
    private LoginUserDetailsService loginUserDetailsService;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        try {
            // 验证 token
            if (Arrays.stream(openUrl).noneMatch(el -> antPathMatcher.match(el, url))) {
                this.checkToken(request);
            }
        } catch (AuthenticationException e) {
            // 交给自定义 AuthentFailureHandler 错误处理器
            //loginFailureHandler.onAuthenticationFailure(request, response, e);
            userLoginFilter.unsuccessfulAuthentication(request,response,e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 验证 token
     * @param request
     */
    private void checkToken(HttpServletRequest request) {
        String token = request.getHeader(Constant.User.ACCESS_TOKEN);
        Assert.assertNotNull(token);
        String username = JwtTokenUtil.getSubject(token);// 获取用户名
        Assert.assertNotNull(username,new SecurityException(ResponseCode.TOKEN_NO_AVAIL.getMessage()));
        String userTokenKey = Constant.User.USER_TOKEN_KEY + username;
        String userKey = loginUserDetailsService.getEntryCacheKey(username);
        String permissionKey = loginUserDetailsService.getPermissionCacheKey(username);
        Assert.isTure(redisUtil.exists(userKey) && redisUtil.exists(permissionKey) && redisUtil.exists(userTokenKey),
                new SecurityException(ResponseCode.SYSTEM_USERNAME_OFFLINE.getMessage()));
        Assert.isTure(!(Strings.isNullOrEmpty(token) || Strings.isNullOrEmpty(username) || !Objects.equals(token,redisUtil.get(userTokenKey)) || !JwtTokenUtil.validate(token)),
                new SecurityException(ResponseCode.TOKEN_NO_AVAIL.getMessage()));

        UserDetails userDetails = null;
        try {
            userDetails = loginUserDetailsService.handleUserDetails(
                    JSON.parseObject(redisUtil.get(userKey), SysUser.class),
                    JSON.parseArray(redisUtil.get(permissionKey), SysPermission.class));
        } catch (Exception e) {
            logger.error("缓存获取用户信息失败，{}",e);
        }finally {
            Assert.assertNotNull(userDetails,new SecurityException(ResponseCode.TOKEN_ERROR.getMessage()));
        }
        // 把验证通过的 token 交给 security
        //UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        // 把 request 交给 security
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 设置为已登录
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
