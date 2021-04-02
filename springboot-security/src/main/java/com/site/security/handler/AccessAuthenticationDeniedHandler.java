package com.site.security.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.site.common.R;
import com.site.common.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 认证用户无权限访问资源时处理器
 */
@Slf4j
@Component("accessAuthenticationDeniedHandler")
public class AccessAuthenticationDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        responseJson(response, ResponseCode.NOT_PERMISSION.getCode(),ResponseCode.NOT_PERMISSION.getMessage());
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
            log.error("【认证用户无权限访问输出异常】,{}",e);
        }
    }
}
