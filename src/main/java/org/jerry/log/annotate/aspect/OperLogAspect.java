package org.jerry.log.annotate.aspect;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.jerry.log.annotate.annotation.Log;
import org.jerry.log.config.LogProperties;
import org.jerry.log.business.entity.SysLog;
import org.jerry.log.business.service.IOperLogService;
import org.jerry.log.toolkit.tool.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class OperLogAspect {

    @Resource
    private IOperLogService operLogService;

    @Resource
    private LogProperties dbScanClass;

    //为了记录方法的执行时间
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    Snowflake snowflake = IdUtil.createSnowflake(1, 1);

    /**
     * 设置操作日志切入点，这里介绍两种方式：
     * 1、基于注解切入（也就是打了自定义注解的方法才会切入）
     *
     * @Pointcut("@annotation(org.wujiangbo.annotation.MyLog)") 2、基于包扫描切入
     * @Pointcut("execution(public * org.wujiangbo.controller..*.*(..))")
     */
    @Pointcut("@annotation(org.jerry.log.annotate.annotation.Log)")//在注解的位置切入代码
    public void operLogPoinCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param result    返回结果
     */
    @AfterReturning(value = "operLogPoinCut()", returning = "result")
    public void saveOperLog(JoinPoint joinPoint, Object result) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        try {
            startTime.set(System.currentTimeMillis());
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            Log myLog = method.getAnnotation(Log.class);
            String id = snowflake.nextId() + "";
            String title;
            String content;
            if (myLog != null) {
                title = myLog.title();//设置模块名称
                content = myLog.content();//设置日志内容
            } else {
                title = null;
                content = null;//设置日志内容
            }
            // 将入参转换成json
            String params = RequestTool.argsArrayToString(joinPoint.getArgs());
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            //设置请求方式
            assert request != null;
            String requestMethod = request.getMethod();
            // 返回结果
            String resultStr = JSON.toJSONString(result);
            // IP地址
            String IP = IPTool.getIp(request);
            //请求URI
            String requestURI = request.getRequestURI();
            //时间
            LocalDateTime date = LocalDateTime.now();
            //记录方法执行耗时时间（单位：毫秒）
            Long takeTime = System.currentTimeMillis() - startTime.get();
            //操作状态（0正常 1异常）
            Integer status = 0;
            String plaintext = null;
            String userId = null;
            String userName = null;
            if (dbScanClass.getIsGetUserInfo()) {
                String ciphertext = null;
                switch (dbScanClass.getAddress()) {
                    case COOKIE:
                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (cookie.getName().equals(dbScanClass.getUserInfoKey())) {
                                    ciphertext = cookie.getValue();
                                }
                            }
                        }
                        break;
                    case SESSION:
                        HttpSession httpSession = request.getSession();
                        if (httpSession.getAttribute(dbScanClass.getUserInfoKey()) == null) {
                            ciphertext = null;
                        } else {
                            ciphertext = httpSession.getAttribute(dbScanClass.getUserInfoKey()) + "";
                        }
                        break;

                }
                assert ciphertext != null;
                ciphertext = URLDecoder.decode(ciphertext, "utf-8");
                if (StringUtils.hasText(ciphertext)) {
                    switch (dbScanClass.getEncryptType()) {
                        case AES:
                            plaintext = AESTool.decrypt(ciphertext, dbScanClass.getKey());
                            break;
                        case DES:
                            plaintext = DESTool.decrypt(dbScanClass.getKey(), ciphertext);
                            break;
                        case DES3:
                            plaintext = DESedeTool.decrypt(ciphertext,dbScanClass.getKey());
                            break;
                    }
                }
                if (StringUtils.hasText(plaintext)) {
                    JSONObject userInfo = JSON.parseObject(plaintext);
                    userId = userInfo.getString("user_id");
                    userName = userInfo.getString("user_name");
                }
            }
            String possession = IPTool.getIpPossession(IP);
            SysLog sysLog = new SysLog(id, title, content, methodName, requestMethod, userId, userName, requestURI, IP, possession, params, resultStr, status, null, date, takeTime);
            operLogService.save(sysLog);
        } catch (Exception e) {
            log.error("异常信息：{}", e.getMessage());
        }
    }


    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && @annotation(org.jerry.log.annotate.annotation.Log)")
    public void operExceptionLogPoinCut() {
    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     */
    @AfterThrowing(pointcut = "operExceptionLogPoinCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        try {
            startTime.set(System.currentTimeMillis());
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName + "()";
            // 获取操作
            Log myLog = method.getAnnotation(Log.class);
            String id = snowflake.nextId() + "";
            //设置模块名称
            String title = null;
            //设置日志内容
            String content = null;
            if (myLog != null) {
                title = myLog.title();
                content = myLog.content();
            }
            // 将入参转换成json
            String params = RequestTool.argsArrayToString(joinPoint.getArgs());
            //设置请求方式
            assert request != null;
            String requestMethod = request.getMethod();
            // IP地址
            String IP = IPTool.getIp(request);
            //请求URI
            String requestURI = request.getRequestURI();
            //时间
            LocalDateTime date = LocalDateTime.now();
            //记录方法执行耗时时间（单位：毫秒）
            Long takeTime = System.currentTimeMillis() - startTime.get();
            //操作状态（0正常 1异常）
            Integer status = 1;
            //记录异常信息
            String errorMsg = RequestTool.stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace(), 2000);
            String plaintext = null;
            String userId = null;
            String userName = null;
            if (dbScanClass.getIsGetUserInfo()) {
                String ciphertext = null;
                switch (dbScanClass.getAddress()) {
                    case COOKIE:
                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (cookie.getName().equals(dbScanClass.getUserInfoKey())) {
                                    ciphertext = cookie.getValue();
                                }
                            }
                        }
                        break;
                    case SESSION:
                        HttpSession httpSession = request.getSession();
                        if (httpSession.getAttribute(dbScanClass.getUserInfoKey()) == null) {
                            ciphertext = null;
                        } else {
                            ciphertext = httpSession.getAttribute(dbScanClass.getUserInfoKey()) + "";
                        }
                        break;

                }
                assert ciphertext != null;
                ciphertext = URLDecoder.decode(ciphertext, "utf-8");
                if (StringUtils.hasText(ciphertext)) {
                    switch (dbScanClass.getEncryptType()) {
                        case AES:
                            plaintext = AESTool.decrypt(ciphertext, dbScanClass.getKey());
                            break;
                        case DES:
                            plaintext = DESTool.decrypt(dbScanClass.getKey(), ciphertext);
                            break;
                        case DES3:
                            plaintext = DESedeTool.decrypt(ciphertext,dbScanClass.getKey());
                            break;
                    }
                }
                if (StringUtils.hasText(plaintext)) {
                    JSONObject userInfo = JSON.parseObject(plaintext);
                    userId = userInfo.getString("user_id");
                    userName = userInfo.getString("user_name");
                }
            }
            String possession = IPTool.getIpPossession(IP);
            SysLog sysLog = new SysLog(id, title, content, methodName, requestMethod, userId, userName, requestURI, IP, possession, params, null, status, errorMsg, date, takeTime);
            operLogService.save(sysLog);
        } catch (Exception e2) {
            log.error("异常信息：{}", e2.getMessage());
        }
    }
}
