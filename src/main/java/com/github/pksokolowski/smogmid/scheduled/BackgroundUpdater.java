package com.github.pksokolowski.smogmid.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackgroundUpdater {

    /**
     * this update is fired every 60 minutes at thirty minutes after the last full hour.
     */
    @Scheduled(cron = "0 30 * * * ? *")
    public void updateAirQualityIndexes(){

    }
}
