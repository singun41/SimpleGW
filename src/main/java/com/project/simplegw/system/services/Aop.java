package com.project.simplegw.system.services;

// import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class Aop {
    
    // private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LoggingAop loggingAop;

    @Autowired
    public Aop(LoggingAop loggingAop) {
        this.loggingAop = loggingAop;
    }


    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Basecode ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    @Pointcut("execution(* com.project.simplegw.common.services.BasecodeController.*(..))")
    private void allOfBasecodeControllerPointcut() {}

    @Around("allOfBasecodeControllerPointcut()")
    public Object allOfBasecodeControllerLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "BasecodeController");
        
        return result;
    }


    @Pointcut("execution(* com.project.simplegw.common.services.BasecodeService.*(..))")
    private void allOfBasecodeServicePointcut() {}

    @Around("allOfBasecodeServicePointcut()")
    public Object allOfBasecodeServiceLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "BasecodeService");
        
        return result;
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Basecode ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Document ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    @Pointcut("execution(* com.project.simplegw.document.services.DocsController.*(..))")
    private void allOfDocsControllerPointcut() {}

    @Around("allOfDocsControllerPointcut()")
    public Object allOfDocsControllerLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "DocsController");
        
        return result;
    }


    @Pointcut("execution(* com.project.simplegw.document.services.DocsService.*(..))")
    private void allOfDocsServicePointcut() {}

    @Around("allOfDocsServicePointcut()")
    public Object allOfDocsServiceLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "DocsService");
        
        return result;
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Document ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    @Pointcut("execution(* com.project.simplegw.approval.services.ApprovalService.*(..))")
    private void allOfApprovalServicePointcut() {}

    @Around("allOfApprovalServicePointcut()")
    public Object allOfApprovalServiceLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "ApprovalService");
        
        return result;
    }


    @Pointcut("execution(* com.project.simplegw.approval.services.ApprovalController.*(..))")
    private void allOfApprovalControllerPointcut() {}

    @Around("allOfApprovalControllerPointcut()")
    public Object allOfApprovalControllerLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "ApprovalController");
        
        return result;
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Approval ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    @Pointcut("execution(* com.project.simplegw.schedule.services.ScheduleController.*(..))")
    private void allOfScheduleControllerPointcut() {}

    @Around("allOfScheduleControllerPointcut()")
    public Object allOfScheduleControllerLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "ScheduleController");
        
        return result;
    }


    @Pointcut("execution(* com.project.simplegw.schedule.services.ScheduleService.*(..))")
    private void allOfScheduleServicePointcut() {}

    @Around("allOfScheduleServicePointcut()")
    public Object allOfScheduleServiceLogging(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        Object result = pjp.proceed();
        sw.stop();
        
        long collapsedTime = sw.getTotalTimeMillis();
        loggingAop.logging(pjp, collapsedTime, "ScheduleService");
        
        return result;
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // url에 더블슬래시(//)나 세미콜론(;)이 포함된 url을 호출하는 경우가 있는데 이 때 exception log가 길게 나오는 걸 없애기 위해서 추가함.
    // 출처: https://isntyet.github.io/java/RequestRejectedException-%ED%95%B8%EB%93%A4%EB%A7%81/
    // @Pointcut("execution(public void org.springframework.security.web.FilterChainProxy.doFilter(..))")
    // private void urlBlackListFiltering() {}

    // @Around("urlBlackListFiltering()")
    // public Object urlBlackListFilteringLogging(ProceedingJoinPoint pjp) throws Throwable {
    //     Object result = null;
    //     try {
    //         result = pjp.proceed();
    //     } catch(Exception e) {
    //         HttpServletResponse response = (HttpServletResponse) pjp.getArgs()[1];
    //         response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    //         logger.warn("URL Black List Error: {}", e.getMessage());
    //     }
    //     return result;
    // }
}
