package com.example.farmingcreditbackend.aspect;

import com.example.farmingcreditbackend.entity.OperationLog;
import com.example.farmingcreditbackend.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志 AOP 切面
 */
@Aspect
@Component
public class LogAspect {
    
    @Autowired
    private OperationLogService operationLogService;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Pointcut("execution(* com.example.farmingcreditbackend.controller..*(..))")
    public void logPointcut() {
    }
    
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 创建日志对象
        OperationLog log = new OperationLog();
        log.setOperationMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURI());
        log.setIpAddress(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        
        // 尝试获取用户信息（可能未登录）
        try {
            // 从请求头中获取用户信息（JWT 过滤器会设置这些属性）
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");
            
            if (userId != null && !userId.isEmpty()) {
                log.setUserId(Long.parseLong(userId));
            }
            if (username != null && !username.isEmpty()) {
                log.setUsername(username);
            }
        } catch (Exception e) {
            // 如果无法获取用户信息，设置为默认值（匿名访问）
            log.setUserId(0L);
            log.setUsername("anonymous");
        }
        
        try {
            // 序列化请求参数
            try {
                log.setRequestParams(objectMapper.writeValueAsString(point.getArgs()));
            } catch (Exception e) {
                log.setRequestParams("参数序列化失败");
            }
            
            // 执行方法
            Object result = point.proceed();
            
            // 记录成功
            log.setStatus(1);
            log.setExecutionTime(System.currentTimeMillis() - startTime);
            
            // 序列化响应数据
            try {
                log.setResponseData(objectMapper.writeValueAsString(result));
            } catch (Exception e) {
                log.setResponseData("响应序列化失败");
            }
            
        } catch (Exception e) {
            // 记录异常
            log.setStatus(0);
            log.setErrorMessage(e.getMessage());
            log.setExecutionTime(System.currentTimeMillis() - startTime);
            throw e;
        } finally {
            // 保存日志
            operationLogService.saveLog(log);
        }
        
        return point.proceed();
    }
}
