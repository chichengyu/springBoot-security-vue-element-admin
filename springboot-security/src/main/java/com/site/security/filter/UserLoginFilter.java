package com.site.security.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.site.common.R;
import com.site.common.config.TokenConfig;
import com.site.common.constant.Constant;
import com.site.common.enums.ResponseCode;
import com.site.common.exception.Assert;
import com.site.controller.UserController;
import com.site.pojo.SysUser;
import com.site.security.JwtAuthenticationToken;
import com.site.security.SecurityException;
import com.site.util.JwtTokenUtil;
import com.site.util.RedisUtil;
import com.site.vo.resp.LoginRespVo;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ??????????????????????????????
 */
public class UserLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final String DEFAULT_USERNAME_PARAMETER = "username";
    private final String DEFAULT_PASSWORD_PARAMETER = "password";
    private final String DEFAULT_VERIFYCODE_PARAMETER = "code";

    @Setter
    private RedisUtil redisUtil;
    @Setter
    private TokenConfig tokenConfig;

    public UserLoginFilter(String loginUrl) {
        super(new AntPathRequestMatcher(loginUrl, HttpMethod.POST.name()));
    }

    /**
     * ??????
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!Objects.equals(request.getMethod(),HttpMethod.POST.name())){
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = this.obtainUsername(request);
        String password = this.obtainPassword(request);
        String code = this.obtainVerifycode(request);
        if (!Objects.isNull(request.getContentType()) && request.getContentType().toString().contains(MediaType.APPLICATION_JSON_VALUE)){
            try (InputStream in = request.getInputStream()){
                Map<String, String> map = new ObjectMapper().readValue(in, new TypeReference<Map<String, String>>() {});
                username = map.get(DEFAULT_USERNAME_PARAMETER);
                password = map.get(DEFAULT_PASSWORD_PARAMETER);
                code = map.get(DEFAULT_VERIFYCODE_PARAMETER);
            } catch (IOException e) {
                logger.error("????????????json????????????",e);
            }
        }
        this.checkCaptcha(request,code);
        // ????????????????????????????????????????????? token????????????????????? antRequest = new EmailAuthenticationToken????????????????????? EmailAuthenticationToken
        JwtAuthenticationToken antRequest = new JwtAuthenticationToken(username,password);
        antRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(antRequest);
    }

    /**
     * ????????????
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SysUser user = (SysUser) authResult.getPrincipal();
        this.successful(response,user);
    }

    /**
     * ????????????
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        this.failMessage(response,failed);
    }

    /**
     * ???????????????
     * @param request
     */
    private void checkCaptcha(HttpServletRequest request,String code) {
        String verifyCode = obtainVerifycode(request);
        if (StringUtils.isBlank(verifyCode)){
            verifyCode = code;
        }
        Assert.isTure(!StringUtils.isBlank(verifyCode) || !StringUtils.isBlank(code),new SecurityException(ResponseCode.SYSTEM_VERIFY_CODE_NOT_EMPTY.getMessage()));
        Assert.isTure(verifyCode.equalsIgnoreCase((String) request.getSession().getAttribute(UserController.CAPTCHA_CODE)),new SecurityException(ResponseCode.SYSTEM_VERIFY_CODE_ERROR.getMessage()));
    }

    private String obtainUsername(HttpServletRequest request) {
        String username = request.getParameter(DEFAULT_USERNAME_PARAMETER);
        if (username == null){
            username = "";
        }
        return username;
    }

    private String obtainPassword(HttpServletRequest request) {
        String password = request.getParameter(DEFAULT_PASSWORD_PARAMETER);
        if (password == null){
            password = "";
        }
        return password;
    }

    private String obtainVerifycode(HttpServletRequest request) {
        String verifycode = request.getParameter(DEFAULT_VERIFYCODE_PARAMETER);
        if (verifycode == null){
            verifycode = "";
        }
        return verifycode;
    }

    private void successful(HttpServletResponse response, SysUser user){
        // 1.?????? token
        JwtTokenUtil.setTokenSettings(tokenConfig);
        HashMap<String,Object> map = Maps.newHashMap();
        map.put(Constant.User.JWT_USER_ID,user.getId());
        String token = JwtTokenUtil.generateToken(user.getUsername(), map);
        redisUtil.set(Constant.User.USER_TOKEN_KEY + user.getUsername(),token);
        LoginRespVo loginRespVo = new LoginRespVo();
        BeanUtils.copyProperties(user,loginRespVo);
        loginRespVo.setAccessToken(token);
        loginRespVo.setNickname(user.getNickName());
        loginRespVo.setMenus(user.getMenus());
        loginRespVo.setPermissions(user.getPermissions());
        this.responseJson(response, R.ok(loginRespVo));
    }

    private void failMessage(HttpServletResponse response, AuthenticationException e){
        int code = ResponseCode.ERROR.getCode();
        String msg = "";
        if (e instanceof InternalAuthenticationServiceException){
            msg = ResponseCode.SYSTEM_USERNAME_NOT_EXISTS.getMessage();// "???????????????,????????????";
        }else if (e instanceof AccountExpiredException){
            msg = ResponseCode.SYSTEM_USERNAME_EXPIRED.getMessage();//"????????????,????????????";
        }else if (e instanceof BadCredentialsException){
            msg = ResponseCode.SYSTEM_PASSWORD_ERROR.getMessage();//"????????????????????????,????????????";
        }else if (e instanceof CredentialsExpiredException){
            msg = ResponseCode.SYSTEM_PASSWORD_EXPIRED.getMessage();//"????????????,????????????";
        }else if(e instanceof LockedException){
            msg = ResponseCode.SYSTEM_USERNAME_LOCKED.getMessage();//"????????????,????????????";
        }else if (e instanceof DisabledException){
            msg = ResponseCode.SYSTEM_USERNAME_DISABLED.getMessage();//"???????????????,????????????";
        }else if (e instanceof UsernameNotFoundException || e instanceof SecurityException){
            msg = e.getMessage();
            if (e instanceof SecurityException){
                code = ResponseCode.TOKEN_NO_AVAIL.getCode();
            }
        }else {
            msg = "????????????";
        }
        this.responseJson(response, R.error(code,msg));
    }

    private void responseJson(HttpServletResponse response,Object data){
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            // ??????????????????????????????????????????????????????false???????????? SerializerFeature.DisableCircularReferenceDetect ?????????????????????
            writer.write(JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect));
            writer.close();
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("????????? JSON ????????????{}",e);
        }
    }
}
