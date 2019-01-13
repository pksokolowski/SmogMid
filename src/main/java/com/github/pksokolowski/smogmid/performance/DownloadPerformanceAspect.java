package com.github.pksokolowski.smogmid.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;


@Aspect
@Configuration
public class DownloadPerformanceAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.github.pksokolowski.smogmid.scheduled.DataUpdater.updateAirQualityIndexes(..))")
    public void aroundDownload(ProceedingJoinPoint joinPoint) throws Throwable {
        var startTime = System.currentTimeMillis();
        joinPoint.proceed();
        var duration = System.currentTimeMillis() - startTime;

        logger.info("Data update routine executed, took {} milliseconds", duration);
    }
}
