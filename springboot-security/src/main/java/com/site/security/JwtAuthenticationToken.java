package com.site.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 定义 token 类型，默认账号密码类型 UsernamePasswordAuthenticationToken
 *
 * 如 Provider 需使用自定义验证逻辑，需要继承 AbstractAuthenticationToken 接口
 * *******注意：此处是一个坑，构造方法必须调用父级方法设置true， super.setAuthenticated(true);必须，
 *             看源码 UsernamePasswordAuthenticationToken 处也有说明必须
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;

    public JwtAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
    }

    public JwtAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);// 注意：此处是一个坑，必须调用父级设置true，看源码 UsernamePasswordAuthenticationToken 处也有说明必须
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
