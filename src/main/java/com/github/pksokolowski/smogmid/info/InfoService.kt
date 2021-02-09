package com.github.pksokolowski.smogmid.info

import com.github.pksokolowski.smogmid.db.AirQualityLog
import com.github.pksokolowski.smogmid.db.PollutionDetails.Companion.POLLUTANTS_DATA
import com.github.pksokolowski.smogmid.db.UpdateLog
import com.github.pksokolowski.smogmid.info.Info.*
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository
import com.github.pksokolowski.smogmid.repository.UpdateLogsRepository
import com.github.pksokolowski.smogmid.utils.TimeHelper
import org.springframework.stereotype.Service
import java.util.*

@Service
class InfoService(
    private val aqLogsRepository: AirQualityLogsRepository,
    private val updateLogsRepository: UpdateLogsRepository
) {
    val info: Info
        get() {
            val logs = aqLogsRepository.findAll()
            val updateLog = updateLogsRepository.findTopByOrderByTimeStampDesc()
            var countWithIndex = 0
            for ((_, indexLevel) in logs) {
                if (indexLevel > -1) countWithIndex++
            }
            return Info(
                logs.size,
                countWithIndex,
                obtainUpdateTime(updateLog),
                obtainUpdateDuration(updateLog),
                obtainIndexLevelDistribution(logs, countWithIndex),
                obtainKeyPollutantsBreakdown(logs),
                obtainDetails(logs)
            )
        }

    private fun obtainUpdateTime(log: UpdateLog?): String {
        return if (log == null) "n/a" else TimeHelper.getDateTimeStampString(log.timeStamp)
    }

    private fun obtainUpdateDuration(log: UpdateLog?): Int {
        return log?.duration?.toInt() ?: -1
    }

    private fun obtainIndexLevelDistribution(logs: List<AirQualityLog>, countWithIndex: Int): List<PercentageInfo> {
        val results = ArrayList<PercentageInfo>()
        val aqIndexDistribution = intArrayOf(0, 0, 0, 0, 0, 0)
        for ((_, indexLevel) in logs) {
            if (indexLevel == -1) continue
            aqIndexDistribution[indexLevel]++
        }
        if (countWithIndex > 0) {
            for (i in aqIndexDistribution.indices) {
                val count = aqIndexDistribution[i]
                val percentage = calcPercentage(count, countWithIndex)
                results.add(PercentageInfo(i, percentage))
            }
        }
        return results
    }

    private fun obtainKeyPollutantsBreakdown(logs: List<AirQualityLog>): List<KeyPollutantsInfo> {
        val results = ArrayList<KeyPollutantsInfo>()

        // pollutants in array must be ordered as in PollutantDetails.getDetailsArray();
        val keyPollutants = IntArray(7)
        var countOfIndexesOf2AndLarger = 0
        for ((_, indexLevel, details1) in logs) {
            if (indexLevel < 2) continue
            countOfIndexesOf2AndLarger++
            var max = -1
            val details = details1.getDetailsArray()
            for (detail in details) {
                if (detail > max) {
                    max = detail
                }
            }
            // increment counts for all pollutants reaching the max level observed
            for (i in keyPollutants.indices) {
                if (details[i] == max) keyPollutants[i]++
            }
        }
        for (pollutant in POLLUTANTS_DATA) {
            val name = pollutant.name
            val count = keyPollutants[pollutant.indexInDetailsArray]
            val percentage = calcPercentage(count, countOfIndexesOf2AndLarger)
            results.add(KeyPollutantsInfo(name, count, percentage))
        }
        return results
    }

    private fun obtainDetails(logs: List<AirQualityLog>): List<DetailsInfo> {
        val results = ArrayList<DetailsInfo>()
        for ((id, index, details1, location) in logs) {
            val details = details1.encode()
            val mapAddress = String.format(
                Locale.US,
                "https://www.google.com/maps/search/?api=1&query=%f,%f",
                location.latitude,
                location.longitude
            )
            val linkText = String.format("%d: %d  (%d)", id, index, details)
            results.add(DetailsInfo(id, index, details, mapAddress))
        }
        return results
    }

    companion object {
        private fun calcPercentage(numerator: Int, denominator: Int): Int {
            return if (denominator == 0) -1 else (100 * (numerator / denominator.toDouble())).toInt()
        }
    }
}