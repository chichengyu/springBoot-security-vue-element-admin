package com.site.security;

import com.site.common.config.TokenConfig;
import com.site.security.filter.JwtTokenAuthenticationFilter;
import com.site.security.filter.UserLoginFilter;
import com.site.security.handler.AccessAuthenticationDeniedHandler;
import com.site.security.handler.AccessAuthenticationEntryPoint;
import com.site.security.handler.CustomLogoutSuccessHandler;
import com.site.security.provider.JwtAuthenticationProvider;
import com.site.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Value("${security.loginUrl}")
    private String loginUrl;
    @Value("${security.logoutUrl}")
    private String logoutUrl;
    @Value("${security.openUrl}")
    private String[] openUrl;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Autowired
    private AccessAuthenticationEntryPoint accessAuthenticationEntryPoint;
    @Autowired
    private AccessAuthenticationDeniedHandler accessAuthenticationDeniedHandler;
    @Autowired
    private LoginUserDetailsService loginUserDetailsService;

    private String[] getPubPath(){
        String[] urls = {
                "/**/*.css","/**/*.js","/favicon.ico",
                "/druid/**","/webjars/**","/v2/api-docs","/swagger/**","/swagger-resources/**","/swagger-ui.html"
        };
        String[] path = new String[openUrl.length + urls.length];
        System.arraycopy(openUrl,0,path,0,openUrl.length);
        System.arraycopy(urls,0,path,openUrl.length,urls.length);
        return path;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    /** 配置认证用户信息和权限
     * 自定义 userDetailsService
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自定义了一套认证 JwtAuthenticationToken / JwtAuthenticationProvider / LoginFilter,
        // 并且保留了原来默认的 UsernamePasswordAuthenticationToken / UsernamePasswordAuthenticationFilter
        //auth.userDetailsService(loginUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(new JwtAuthenticationProvider(loginUserDetailsService,bCryptPasswordEncoder()))
            .userDetailsService(loginUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    // 登录过滤器 注意：这里注释的那个坑，自定义过滤器时必须设置成功处理器
    @Bean
    public UserLoginFilter userLoginFilter() throws Exception {
        UserLoginFilter userLoginFilter = new UserLoginFilter(loginUrl);
        userLoginFilter.setAuthenticationManager(authenticationManagerBean());
        //userLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);// 自定义过滤器时必须设置成功处理器，否则登录成功后会未登录
        //userLoginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        userLoginFilter.setRedisUtil(redisUtil);
        userLoginFilter.setTokenConfig(tokenConfig);
        return userLoginFilter;
    }

    // 认证过滤器
    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() throws Exception {
        JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter = new JwtTokenAuthenticationFilter();
        jwtTokenAuthenticationFilter.setRedisUtil(redisUtil);
        jwtTokenAuthenticationFilter.setOpenUrl(getPubPath());
        jwtTokenAuthenticationFilter.setUserLoginFilter(userLoginFilter());
        jwtTokenAuthenticationFilter.setLoginUserDetailsService(loginUserDetailsService);
        return jwtTokenAuthenticationFilter;
    }

    /** 配置拦截请求资源
     * 成功与失败处理器
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 注意 filter 顺序，将登录过滤器放在验证过滤器前面，但需要先注册验证过滤器
        //http.addFilterAt(jwtTokenAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)// 替换 UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(userLoginFilter(),JwtTokenAuthenticationFilter.class)
            .formLogin() // 表单模式
            .loginProcessingUrl(loginUrl)
            //.successHandler(loginSuccessHandler) // 登录成功处理器
            //.failureHandler(loginFailureHandler) // 登录失败处理器
            .and()
            .logout().logoutUrl(logoutUrl).logoutSuccessHandler(customLogoutSuccessHandler)
            .and()
            .cors()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable().cacheControl().disable() // 开启允许iframe 嵌套。security默认禁用firam跨域与缓存
            .and()
            // 这里使用 token，定制我们自己的 session 策略，调整为让 Spring Security 不创建和使用 session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 跨域预检请求
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            //.antMatchers("/**/*.css","/**/*.js","/favicon.ico").permitAll()
            //.antMatchers("/druid/**","/webjars/**","/*/api-docs","/swagger/**","/swagger-resources/**","/swagger-ui.html").permitAll()
            //.antMatchers("/api/user/login","/api/user/captcha").permitAll()
            //.antMatchers(openUrl).permitAll()
            .antMatchers(getPubPath()).permitAll()
            .anyRequest().authenticated() //所有请求，必须授权才能访问
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(accessAuthenticationEntryPoint) // 匿名用户无权限访问资源时处理器
            .accessDeniedHandler(accessAuthenticationDeniedHandler); // 认证用户无权限访问资源时处理器
    }
}
