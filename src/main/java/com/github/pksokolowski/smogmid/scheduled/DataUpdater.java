package com.github.pksokolowski.smogmid.scheduled;

import com.github.pksokolowski.smogmid.api.AQIDownloader;
import com.github.pksokolowski.smogmid.db.UpdateLog;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import com.github.pksokolowski.smogmid.repository.UpdateLogsRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class DataUpdater {

    private AQIDownloader aqiDownloader;
    private AirQualityLogsRepository aqLogsRepository;
    private UpdateLogsRepository updateLogsRepository;

    // an object solely for synchronization of the update method.
    private final Object lock = new Object();

    public DataUpdater(AQIDownloader aqiDownloader,
                       AirQualityLogsRepository aqLogsRepository,
                       UpdateLogsRepository updateLogsRepository) {
        this.aqiDownloader = aqiDownloader;
        this.aqLogsRepository = aqLogsRepository;
        this.updateLogsRepository = updateLogsRepository;
    }

    /**
     * this update is fired every 60 minutes at thirty minutes after the last full hour.
     * It removes the old logs from the database and replaces them with newly downloaded ones.
     */
    @Scheduled(cron = "0 30 * * * ?")
    private void updateAirQualityIndexes() {
        synchronized (lock) {
            final var startStamp = Calendar.getInstance().getTimeInMillis();
            final var logs = aqiDownloader.getAirQualityLogs();
            final var endStamp = Calendar.getInstance().getTimeInMillis();

            final var duration = endStamp - startStamp;
            final var updateLog = new UpdateLog(startStamp, duration);

            // only need the latest log currently, so the older logs are deleted
            updateLogsRepository.deleteAll();
            updateLogsRepository.save(updateLog);

            aqLogsRepository.deleteAll();
            aqLogsRepository.saveAll(logs);
            aqLogsRepository.flush();
        }
    }

    /**
     * This method is meant to update the data on startup if needed. When data found in the database is old, an update
     * might be done.
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final var calendar = Calendar.getInstance();
        final var now = calendar.getTimeInMillis();
        var latestUpdateLog = updateLogsRepository.findTopByOrderByTimeStampDesc();
        if (latestUpdateLog == null) {
            updateAirQualityIndexes();
            return;
        }
        var timeSinceUpdate = now - latestUpdateLog.getTimeStamp();

        long hourInMillis = 60 * 60000;
        if (timeSinceUpdate > hourInMillis) {
            updateAirQualityIndexes();
        }
    }
}
