package br.com.uppersystems.uptrace.aop;

import br.com.uppersystems.uptrace.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(@org.springframework.web.bind.annotation.GetMapping * *(..))")
    public void logginRestResources() {

    }

    @Around("logginRestResources()")
    public Object logBeforeRestResource(ProceedingJoinPoint joinPoint) throws Throwable{


        Object proceed = null;

        String path = "";
        MethodSignature ms = null;
        System.out.println("Simmmmmm");


        Object klass = joinPoint.getTarget();
        RequestMapping requestMappingResource = klass.getClass().getAnnotation(RequestMapping.class);

        if(ObjectUtils.isNotEmpty(requestMappingResource) && requestMappingResource.value().length > 0){
            path = requestMappingResource.value()[0];
        }

        Object[] args = joinPoint.getArgs();
        ms = (MethodSignature) joinPoint.getSignature();
        Method m = ms.getMethod();

        GetMapping requestMapping = m.getAnnotation(GetMapping.class);

        if(ObjectUtils.isNotEmpty(requestMapping)){

            if(ObjectUtils.isNotEmpty(requestMapping.value()) && requestMapping.value().length > 0){
                path += requestMapping.value()[0];
            }

        }

        TraceContextHolder.getInstance().getActualTrace().setPattern(path);

        proceed = joinPoint.proceed();

        return proceed;

    }
}
