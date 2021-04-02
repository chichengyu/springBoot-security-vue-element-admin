package com.site.security.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.site.common.R;
import com.site.common.constant.Constant;
import com.site.common.enums.ResponseCode;
import com.site.util.JwtTokenUtil;
import com.site.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * 退出登录处理器
 */
@Slf4j
@Component("customLogoutSuccessHandler")
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(auth)){
            new SecurityContextLogoutHandler().logout(request,response,auth);
        }
        String token = request.getHeader(Constant.User.ACCESS_TOKEN);
        if (!Objects.isNull(token)){
            String username = this.getUsername(token);
            if (!Objects.isNull(username)){
                String userKey = Constant.User.USER_KEY + username;
                String userTokenKey = Constant.User.USER_TOKEN_KEY + username;
                String permissionKey = Constant.User.PERMISSION_KEY + username;
                redisUtil.del(userKey);
                redisUtil.del(userTokenKey);
                redisUtil.del(permissionKey);
            }
        }
        this.responseJson(response, ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 返回json
     * @param response
     * @param message
     */
    private void responseJson(HttpServletResponse response,String message){
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try{
            PrintWriter writer = response.getWriter();
            // 消除对同一对象循环引用的问题，默认为false，不设置 SerializerFeature.DisableCircularReferenceDetect 数据可能会乱码
            writer.write(JSON.toJSONString(R.ok(message), SerializerFeature.DisableCircularReferenceDetect));
            writer.close();
            response.flushBuffer();
        }catch (IOException e){
            log.error("【登录失败返回异常】，{}",e);
        }
    }

    /**
     * 获取登录账户username
     * @return
     */
    protected String getUsername(String token){
        return JwtTokenUtil.getSubject(token);
    }
}
