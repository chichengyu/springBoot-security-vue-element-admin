//package com.site.security.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.site.common.R;
//import com.site.common.enums.ResponseCode;
//import com.site.security.SecurityException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AccountExpiredException;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.CredentialsExpiredException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.InternalAuthenticationServiceException;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * 登录失败处理器
// */
//@Slf4j
//@Component("loginAuthenticationFailureHandler")
//public class LoginFailureHandler implements AuthenticationFailureHandler {
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//        String msg = "";
//        if (e instanceof InternalAuthenticationServiceException){
//            msg = ResponseCode.SYSTEM_USERNAME_NOT_EXISTS.getMessage();// "账户不存在,登录失败";
//        }else if (e instanceof AccountExpiredException){
//            msg = ResponseCode.SYSTEM_USERNAME_EXPIRED.getMessage();//"账户过期,登录失败";
//        }else if (e instanceof BadCredentialsException){
//            msg = ResponseCode.SYSTEM_PASSWORD_ERROR.getMessage();//"用户名或密码错误,登录失败";
//        }else if (e instanceof CredentialsExpiredException){
//            msg = ResponseCode.SYSTEM_PASSWORD_EXPIRED.getMessage();//"密码过期,登录失败";
//        }else if(e instanceof LockedException){
//            msg = ResponseCode.SYSTEM_USERNAME_LOCKED.getMessage();//"账户被锁,登录失败";
//        }else if (e instanceof DisabledException){
//            msg = ResponseCode.SYSTEM_USERNAME_DISABLED.getMessage();//"账户被禁用,登录失败";
//        }else if (e instanceof UsernameNotFoundException || e instanceof SecurityException){
//            msg = e.getMessage();
//        }else {
//            msg = "登录失败";
//        }
//        responseJson(response, ResponseCode.ERROR.getCode(),msg);
//    }
//
//    /**
//     * 返回json
//     * @param response
//     * @param code
//     * @param message
//     */
//    private void responseJson(HttpServletResponse response,int code,String message){
//        response.setContentType("application/json;charset=utf-8");
//        response.setCharacterEncoding("UTF-8");
//        try{
//            PrintWriter writer = response.getWriter();
//            // 消除对同一对象循环引用的问题，默认为false，不设置 SerializerFeature.DisableCircularReferenceDetect 数据可能会乱码
//            writer.write(JSON.toJSONString(R.error(code,message), SerializerFeature.DisableCircularReferenceDetect));
//            writer.close();
//            response.flushBuffer();
//        }catch (IOException e){
//            log.error("【登录失败返回异常】，{}",e);
//        }
//    }
//}
