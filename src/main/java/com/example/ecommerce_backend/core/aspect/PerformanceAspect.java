package com.example.ecommerce_backend.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("@annotation(com.example.ecommerce_backend.core.annotation.Monitored) || execution(* com.example.ecommerce_backend.modules..service.*.*(..))")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = signature.getDeclaringType().getSimpleName();
            String methodName = signature.getMethod().getName();
            if (duration > 1000) {
                log.warn("SLOW - {}.{} took {}ms", className, methodName, duration);
            } else if (duration > 200) {
                log.info("PERF - {}.{} took {}ms", className, methodName, duration);
            } else {
                log.debug("PERF - {}.{} took {}ms", className, methodName, duration);
            }
        }
    }
}
