package com.senyu.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* com.senyu.*.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        String className = joinPoint.getTarget().getClass().getName(); // 获取类名
        String methodName = joinPoint.getSignature().getName(); // 获取执行的方法名
        log.info("className is {}, methodName is {}", className, methodName);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        if (totalTime > 3000) {
            log.info("执行时间太长了，耗费了{}ms", totalTime);
        } else if (totalTime > 2000) {
            log.info("执行时间稍微有点长，耗费了{}ms", totalTime);
        } else {
            log.info("执行耗费了{}ms", totalTime);
        }
        return proceed;
    }
}
