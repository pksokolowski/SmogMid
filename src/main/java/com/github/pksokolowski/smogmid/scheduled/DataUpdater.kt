package com.github.pksokolowski.smogmid.scheduled

import com.github.pksokolowski.smogmid.api.AQIDownloader
import com.github.pksokolowski.smogmid.db.UpdateLog
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository
import com.github.pksokolowski.smogmid.repository.UpdateLogsRepository
import com.github.pksokolowski.smogmid.utils.ScopesProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class DataUpdater(
    private val aqiDownloader: AQIDownloader,
    private val aqLogsRepository: AirQualityLogsRepository,
    private val updateLogsRepository: UpdateLogsRepository,
    private val scopesProvider: ScopesProvider
) {
    private val mutex = Mutex()

    /**
     * this update is fired every 60 minutes at thirty minutes after the last full hour.
     * It removes the old logs from the database and replaces them with newly downloaded ones.
     */
    @Scheduled(cron = "0 30 * * * ?")
    private fun updateAirQualityIndexes() {
        scopesProvider.aqUpdatesScope.launch {
            mutex.withLock {
                val startStamp = Calendar.getInstance().timeInMillis
                val logs = aqiDownloader.airQualityLogs
                val endStamp = Calendar.getInstance().timeInMillis
                val duration = endStamp - startStamp
                val updateLog = UpdateLog(startStamp, duration)

                // only need the latest log currently, so the older logs are deleted
                updateLogsRepository.deleteAll()
                updateLogsRepository.save(updateLog)
                aqLogsRepository.deleteAll()
                aqLogsRepository.saveAll(logs)
                aqLogsRepository.flush()
            }
        }
    }

    /**
     * This method is meant to update the data on startup if needed. When data found in the database is old, an update
     * might be done.
     */
    @EventListener
    fun handleContextRefresh(event: ContextRefreshedEvent?) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val latestUpdateLog = updateLogsRepository.findTopByOrderByTimeStampDesc()
        if (latestUpdateLog == null) {
            updateAirQualityIndexes()
            return
        }
        val timeSinceUpdate = now - latestUpdateLog.timeStamp
        val hourInMillis = (60 * 60000).toLong()
        if (timeSinceUpdate > hourInMillis) {
            updateAirQualityIndexes()
        }
    }
}