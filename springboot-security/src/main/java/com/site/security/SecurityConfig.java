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


    /** ?????????????????????????????????
     * ????????? userDetailsService
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // ???????????????????????? JwtAuthenticationToken / JwtAuthenticationProvider / LoginFilter,
        // ?????????????????????????????? UsernamePasswordAuthenticationToken / UsernamePasswordAuthenticationFilter
        //auth.userDetailsService(loginUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(new JwtAuthenticationProvider(loginUserDetailsService,bCryptPasswordEncoder()))
            .userDetailsService(loginUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    // ??????????????? ????????????????????????????????????????????????????????????????????????????????????
    @Bean
    public UserLoginFilter userLoginFilter() throws Exception {
        UserLoginFilter userLoginFilter = new UserLoginFilter(loginUrl);
        userLoginFilter.setAuthenticationManager(authenticationManagerBean());
        //userLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);// ????????????????????????????????????????????????????????????????????????????????????
        //userLoginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        userLoginFilter.setRedisUtil(redisUtil);
        userLoginFilter.setTokenConfig(tokenConfig);
        return userLoginFilter;
    }

    // ???????????????
    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() throws Exception {
        JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter = new JwtTokenAuthenticationFilter();
        jwtTokenAuthenticationFilter.setRedisUtil(redisUtil);
        jwtTokenAuthenticationFilter.setOpenUrl(getPubPath());
        jwtTokenAuthenticationFilter.setUserLoginFilter(userLoginFilter());
        jwtTokenAuthenticationFilter.setLoginUserDetailsService(loginUserDetailsService);
        return jwtTokenAuthenticationFilter;
    }

    /** ????????????????????????
     * ????????????????????????
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ?????? filter ??????????????????????????????????????????????????????????????????????????????????????????
        //http.addFilterAt(jwtTokenAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)// ?????? UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(userLoginFilter(),JwtTokenAuthenticationFilter.class)
            .formLogin() // ????????????
            .loginProcessingUrl(loginUrl)
            //.successHandler(loginSuccessHandler) // ?????????????????????
            //.failureHandler(loginFailureHandler) // ?????????????????????
            .and()
            .logout().logoutUrl(logoutUrl).logoutSuccessHandler(customLogoutSuccessHandler)
            .and()
            .cors()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable().cacheControl().disable() // ????????????iframe ?????????security????????????firam???????????????
            .and()
            // ???????????? token???????????????????????? session ????????????????????? Spring Security ?????????????????? session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // ??????????????????
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            //.antMatchers("/**/*.css","/**/*.js","/favicon.ico").permitAll()
            //.antMatchers("/druid/**","/webjars/**","/*/api-docs","/swagger/**","/swagger-resources/**","/swagger-ui.html").permitAll()
            //.antMatchers("/api/user/login","/api/user/captcha").permitAll()
            //.antMatchers(openUrl).permitAll()
            .antMatchers(getPubPath()).permitAll()
            .anyRequest().authenticated() //???????????????????????????????????????
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(accessAuthenticationEntryPoint) // ?????????????????????????????????????????????
            .accessDeniedHandler(accessAuthenticationDeniedHandler); // ?????????????????????????????????????????????
    }
}
