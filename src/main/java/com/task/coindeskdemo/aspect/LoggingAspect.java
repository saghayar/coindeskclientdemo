package com.task.coindeskdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static com.task.coindeskdemo.util.Constants.AROUND_LOG_MSG;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Pointcut("within(com.task.coindeskdemo.service.impl.*)")
    public void impl() {
        //Does nothing because need this method in order to define Pointcut
    }

    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("impl()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        log.info(AROUND_LOG_MSG.value(), name, System.currentTimeMillis() - start);
        return result;
    }

}
