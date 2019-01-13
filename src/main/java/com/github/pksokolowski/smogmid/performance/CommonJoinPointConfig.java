package com.github.pksokolowski.smogmid.performance;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {

    @Pointcut("@annotation(com.github.pksokolowski.smogmid.performance.TrackPerformance)")
    public void trackPerformanceAnnotation(){}

}
