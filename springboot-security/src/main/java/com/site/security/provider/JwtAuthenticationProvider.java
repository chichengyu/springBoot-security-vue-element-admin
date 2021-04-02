package com.site.security.provider;

import com.site.common.enums.ResponseCode;
import com.site.common.exception.Assert;
import com.site.security.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 自定义身份认证验证组件
 *
 * 如需自定义验证逻辑，需要实现 AuthenticationProvider 接口，实现 authenticate supports 2个方法，authenticate 方法实现自定义验证逻辑
 * 我这里不需要自定义验证逻辑，所以继承 AuthenticationProvider
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public JwtAuthenticationProvider(UserDetailsService userDetailsService,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /** 自定义认证逻辑
     * 登录时会进入到这验证
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Assert.assertNotNull(username,ResponseCode.SYSTEM_USERNAME_NOT_EMPTY.getMessage());
        Assert.assertNotNull(authentication.getCredentials(),new UsernameNotFoundException(ResponseCode.SYSTEM_PASSWORD_ERROR.getMessage()));
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Assert.isTure(bCryptPasswordEncoder.matches(password,userDetails.getPassword()), new BadCredentialsException(ResponseCode.SYSTEM_PASSWORD_ERROR.getMessage()));
        return new JwtAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    /**
     * 验证是否提供输入类型的认证服务
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}


//=========================================== 如需自定义一套验证逻辑，请看下面 ==================================================
// 自定义一套验证 ： 1.首先创建一个 token 身份 EmailAuthenticationToken
//                 2.首先创建一个 email toekn 验证 EmailAuthenticationProvider 来验证 EmailAuthenticationToken
//                 3.在登录 UserLoginFilter 过滤器(继承 AbstractAuthenticationProcessingFilter)中进行 构建 EmailAuthenticationToken 身份token

// ---> example
// --------> 1. EmailAuthenticationToken
//    *****注意：此处又一个坑，通过翻看源码 UsernamePasswordAuthenticationToken
//              可以到构造方中又这么一句 super.setAuthenticated(true); // must use super, as we override
//              意思是创建继承 token 时，必须使用super，因为我们会覆盖，大致意思是会覆盖你的验证状态
//    *****这个坑当时搞的我找了很久的问题，记录一下，防止看到这的小伙伴入坑
//         创建新的 token类时，一定一定千万千万要记得 调用父级的 super.setAuthenticated(true);
//         否则会在登录成功后，访问资源的时候，重复走入到自定义 Provider 的验证逻辑，并且覆盖你的验证状态

// --------> 2. EmailAuthenticationProvider

// --------> 3. 登录 UserLogr 过滤器构建 EmailAuthenticationToken
// 3.在登录 UserLoginFilter 过滤器(继承 AbstractAuthenticationProcessingFilter)中进行 构建 JwtAuthenticationToken 身份