package com.site.security.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.site.common.R;
import com.site.common.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 匿名用户(即未登录时访问资源为匿名访问)无权限处理器
 */
@Slf4j
@Component("accessAuthenticationEntryPoint")
public class AccessAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        responseJson(response, ResponseCode.TOKEN_ERROR.getCode(),ResponseCode.TOKEN_ERROR.getMessage());
    }

    /**
     * 返回json
     * @param response
     * @param code
     * @param message
     */
    private void responseJson(HttpServletResponse response,int code,String message){
        response.setContentType("application/json;charset=utf-8");
        try{
            PrintWriter writer = response.getWriter();
            // 消除对同一对象循环引用的问题，默认为false，不设置 SerializerFeature.DisableCircularReferenceDetect 数据可能会乱码
            writer.write(JSON.toJSONString(R.error(code,message), SerializerFeature.DisableCircularReferenceDetect));
            writer.close();
            response.flushBuffer();
        }catch (IOException e){
            log.error("【匿名用户无权限访问输出异常】,{}",e);
        }
    }
}
