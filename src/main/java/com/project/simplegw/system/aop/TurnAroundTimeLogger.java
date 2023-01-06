package com.project.simplegw.system.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TurnAroundTimeLogger {
    public TurnAroundTimeLogger() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    /*
        각 서비스들의 메서드 소요시간 체크하기.

        필요한 것만 추가하여 사용.
    */


    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @Pointcut("execution(public !void com.project.simplegw.schedule.services.ScheduleService.getScheduleList(..))")
    private void scheduleServicePublicGetScheduleListPointcut() {}

    @Around("scheduleServicePublicGetScheduleListPointcut()")
    public Object aroundScheduleServicePublicGetScheduleListPointcut(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();

        Object result = pjp.proceed();
        sw.stop();

        log.info("ScheduleService's public List<DtosSchedule> getScheduleList(..) turnaround time(ms): {}", sw.getTotalTimeMillis());
        return result;
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- schedule ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
