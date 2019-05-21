package br.com.uppersystems.uptrace.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.uppersystems.uptrace.trace.StackTrace;
import br.com.uppersystems.uptrace.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;

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

        Object klass = joinPoint.getTarget();
        RequestMapping requestMappingResource = klass.getClass().getAnnotation(RequestMapping.class);

        if(!ObjectUtils.isEmpty(requestMappingResource) && requestMappingResource.value().length > 0){
            path = requestMappingResource.value()[0];
        }

        Object[] args = joinPoint.getArgs();
        ms = (MethodSignature) joinPoint.getSignature();
        Method m = ms.getMethod();

        GetMapping requestMapping = m.getAnnotation(GetMapping.class);

        if(!ObjectUtils.isEmpty(requestMapping)){

            if(!ObjectUtils.isEmpty(requestMapping.value()) && requestMapping.value().length > 0){
                path += requestMapping.value()[0];
            }

        }

        TraceContextHolder.getInstance().getActualTrace().setPattern(path);

        Throwable upThrowable = null;
        try {

            proceed = joinPoint.proceed();
        } catch (Throwable throwable) {

            upThrowable = throwable;
        }

        if(upThrowable != null){

        	TraceContextHolder.getInstance().getActualTrace().setMethod(ms.toShortString());
            TraceContextHolder.getInstance().getActualTrace().setPattern(path);
            StackTrace stackTrace = new StackTrace(upThrowable.getClass().getName(), upThrowable.getMessage(), upThrowable.getMessage());
            TraceContextHolder.getInstance().getActualTrace().setStackTrace(stackTrace);
            
            throw upThrowable;
        }

        return proceed;

    }

}
