package com.site.common.aspect;

import com.alibaba.fastjson.JSON;
import com.site.common.annotation.Log;
import com.site.common.constant.Constant;
import com.site.dao.LogDao;
import com.site.pojo.SysLog;
import com.site.util.IdWorker;
import com.site.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

/**
 * 切面
 */
@Slf4j
@Aspect
@Component
public class AspectAdvice {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private LogDao logDao;


    @Around("execution(* com.site.controller..*(..))")
    public Object handleAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始时间
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object[] args = joinPoint.getArgs();
        // 执行方法
        Object proceed = joinPoint.proceed(args);
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - startTime;

        log.info("接口地址{},请求方式{},参数{},时长{}毫秒",request.getRequestURI(),request.getMethod(),args,time);

        // 保存日志
        try {
            saveLog(joinPoint,request,args,time);
        } catch (Exception e) {
            log.error("【记录日志】，{}",e);
        }
        return proceed;
    }

    /**
     * 保存日志
     * @param joinPoint
     * @param request
     * @param args
     * @param time
     */
    private void saveLog(ProceedingJoinPoint joinPoint,HttpServletRequest request,Object[] args,long time){
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        // 查询是否存在 Log 自定义注解
        if (signature.getMethod().isAnnotationPresent(Log.class)){
            String className = joinPoint.getTarget().getClass().getName();// 获取控制器，包含报包名
            String methodName = signature.getName();//方法名称
            SysLog sysLog = new SysLog();
            sysLog.setId(String.valueOf(idWorker.nextId()));
            sysLog.setMethod(className + "." + methodName);
            try {// 可能会出现转换错误
                sysLog.setParams(args.length != 0 ? JSON.toJSONString(args) : "");
            } catch (Exception e) {}
            // 获取自定义注解
            Log recordLog = signature.getMethod().getAnnotation(Log.class);
            if (recordLog != null){
                // 注解上的描述
                sysLog.setOperation(recordLog.title() + "-" + recordLog.action());
            }
            // 设置 ip
            sysLog.setIp(request.getRemoteAddr());
            // 从 token 中获取用户id
            Claims claims = JwtTokenUtil.parseToken(request.getHeader(Constant.User.ACCESS_TOKEN));
            String userId = null;
            String username = null;
            if (claims != null){
                userId = (String) claims.get(Constant.User.JWT_USER_ID);
                username = claims.getSubject();
            }
            sysLog.setUserId(userId);
            sysLog.setUsername(username);
            sysLog.setTime(time);
            sysLog.setCreateTime(new Date());

            log.info("【日志数据】，{}",sysLog.toString());
            logDao.insertSelective(sysLog);
        }
    }
}
