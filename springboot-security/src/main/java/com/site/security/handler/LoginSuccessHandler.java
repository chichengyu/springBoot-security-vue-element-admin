//package com.site.security.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.google.common.collect.Maps;
//import com.site.common.R;
//import com.site.common.config.TokenConfig;
//import com.site.common.constant.Constant;
//import com.site.pojo.SysUser;
//import com.site.util.JwtTokenUtil;
//import com.site.util.RedisUtil;
//import com.site.vo.resp.LoginRespVo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//
///**
// * 登录成功处理器
// */
//@Slf4j
//@Component("loginSuccessHandler")
//public class LoginSuccessHandler implements AuthenticationSuccessHandler{
//
//    @Autowired
//    private TokenConfig tokenConfig;
//    @Autowired
//    private RedisUtil redisUtil;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        // 1.生成 token
//        SysUser user = (SysUser) authentication.getPrincipal();
//        JwtTokenUtil.setTokenSettings(tokenConfig);
//        HashMap<String,Object> map = Maps.newHashMap();
//        map.put(Constant.User.JWT_USER_ID,user.getId());
//        String token = JwtTokenUtil.generateToken(user.getUsername(), map);
//        redisUtil.set(Constant.User.USER_TOKEN_KEY + user.getUsername(),token);
//        LoginRespVo loginRespVo = new LoginRespVo();
//        BeanUtils.copyProperties(user,loginRespVo);
//        loginRespVo.setAccessToken(token);
//        loginRespVo.setNickname(user.getNickName());
//        loginRespVo.setMenus(user.getMenus());
//        loginRespVo.setPermissions(user.getPermissions());
//        response.setContentType("application/json;charset=utf-8");
//        responseJson(response, loginRespVo);
//    }
//
//    /**
//     * 返回 json
//     * @param response
//     * @param data
//     */
//    private void responseJson(HttpServletResponse response,Object data){
//        response.setContentType("application/json;charset=utf-8");
//        response.setCharacterEncoding("UTF-8");
//        try {
//            PrintWriter writer = response.getWriter();
//            // 消除对同一对象循环引用的问题，默认为false，不设置 SerializerFeature.DisableCircularReferenceDetect 数据可能会乱码
//            writer.write(JSON.toJSONString(R.ok(data),SerializerFeature.DisableCircularReferenceDetect));
//            writer.close();
//            response.flushBuffer();
//        } catch (IOException e) {
//            log.error("【输出 JSON 异常】，{}",e);
//        }
//    }
//}
