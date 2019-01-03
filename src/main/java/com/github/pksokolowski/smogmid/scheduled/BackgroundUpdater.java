package com.github.pksokolowski.smogmid.scheduled;

import com.github.pksokolowski.smogmid.api.AQIDownloader;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackgroundUpdater {

    private AQIDownloader aqiDownloader;
    private AirQualityLogsRepository aqLogsRepository;

    public BackgroundUpdater(AQIDownloader aqiDownloader, AirQualityLogsRepository aqLogsRepository){
        this.aqiDownloader = aqiDownloader;
        this.aqLogsRepository = aqLogsRepository;
    }

    /**
     * this update is fired every 60 minutes at thirty minutes after the last full hour.
     * It removes the old logs from the database and replaces them with newly downloaded ones.
     */
    @Scheduled(cron = "0 30 * * * ?")
    public void updateAirQualityIndexes(){
        final var logs = aqiDownloader.getAirQualityLogs();
        aqLogsRepository.deleteAll();
        aqLogsRepository.saveAll(logs);
        aqLogsRepository.flush();
    }
}
