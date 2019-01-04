package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.db.PollutionDetails;
import com.github.pksokolowski.smogmid.db.PollutionDetails.PollutantData;
import com.github.pksokolowski.smogmid.db.UpdateLog;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import com.github.pksokolowski.smogmid.repository.UpdateLogsRepository;
import com.github.pksokolowski.smogmid.utils.TimeHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
public class InfoController {

    private AirQualityLogsRepository aqLogsRepository;
    private UpdateLogsRepository updateLogsRepository;

    public InfoController(AirQualityLogsRepository aqLogsRepository, UpdateLogsRepository updateLogsRepository) {
        this.aqLogsRepository = aqLogsRepository;
        this.updateLogsRepository = updateLogsRepository;
    }

    @RequestMapping("/info")
    public String getInfo() {
        var logs = aqLogsRepository.findAll();
        var updateLog = updateLogsRepository.findTopByOrderByTimeStampDesc();
        int countWithIndex = 0;
        for (AirQualityLog log : logs) {
            if (log.getIndexLevel() > -1) countWithIndex++;
        }

        return getBasicInfo(logs, updateLog, countWithIndex) +
                getPercentagesOfIndexLevels(logs, countWithIndex) +
                getKeyPollutantStats(logs) +
                getListOfStationsAsLinks(logs);
    }

    /*
     * generates some general information about the provided data
     * Example output:
     *
     * Info page
     * Logs count: 174
     * Logs with indexes: 149
     * Update time: 20:30 04.01.19
     * Update duration in millis: 42095
     *
     */
    private static String getBasicInfo(List<AirQualityLog> logs, UpdateLog updateLog, int countWithIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append("<body bgcolor=\"#000000\" text=\"white\" link=\"white\" vlink=\"beige\"/>");
        sb.append("Info page");
        sb.append("<br>Logs count: ").append(logs.size());
        sb.append("<br>Logs with indexes: ").append(countWithIndex);
        if (updateLog != null) {
            final var updateTime = TimeHelper.getDateTimeStampString(updateLog.getTimeStamp());
            sb.append("<br>Update time: ").append(updateTime);
            sb.append("<br>Update duration in millis: ").append(updateLog.getDuration());
        }
        return sb.toString();
    }

    /*
     * generates a list of percentages of stations with each of the possible air quality index levels
     * Example output:
     *
     * Percentages of specific index levels:
     * 0 : 32%
     * 1 : 58%
     * 2 : 8%
     * 3 : 0%
     * 4 : 0%
     * 5 : 0%
     *
     */
    private static String getPercentagesOfIndexLevels(List<AirQualityLog> logs, int countWithIndex) {
        StringBuilder sb = new StringBuilder();
        // get air quality index levels distribution
        var aqIndexDistribution = new int[]{0, 0, 0, 0, 0, 0};
        for (AirQualityLog log : logs) {
            var indexLevel = log.getIndexLevel();
            if (indexLevel == -1) continue;
            aqIndexDistribution[indexLevel]++;
        }
        sb.append("<br><br>Percentages of specific index levels:");
        if (countWithIndex > 0) {
            for (int i = 0; i < aqIndexDistribution.length; i++) {
                int count = aqIndexDistribution[i];
                var percentage = calcPercentage(count, countWithIndex);
                sb.append(String.format("<br> %d : %s", i, percentage));
            }
        }
        return sb.toString();
    }

    /*
     * generates a list amounts and percentages of cases where each of the monitored pollutants
     * was found to have the highest index level on a given station. Uses only the data from stations
     * where the highest index level is moderate (2) or higher, so it doesn't take stations with clean
     * air into account.
     * Example output:
     *
     * Key pollutant stats for stations with AQ index level of 2 (moderate) or greater:
     * PM10 : 2 (15%)
     * PM25 : 13 (100%)
     * O3 : 0 (0%)
     * NO2 : 0 (0%)
     * SO2 : 0 (0%)
     * C6H6 : 0 (0%)
     * CO : 0 (0%)
     *
     */
    private static String getKeyPollutantStats(List<AirQualityLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br><br>Key pollutant stats for stations with AQ index level of 2 (moderate) or greater:");

        // pollutants in array must be ordered as in PollutantDetails.getDetailsArray();
        final var keyPollutants = new int[7];
        var countOfIndexesOf2AndLarger = 0;
        for (AirQualityLog log : logs) {
            if (log.getIndexLevel() < 2) continue;
            countOfIndexesOf2AndLarger++;
            var max = -1;
            var details = log.getDetails().getDetailsArray();
            for (Integer detail : details) {
                if (detail > max) {
                    max = detail;
                }
            }
            // increment counts for all pollutants reaching the max level observed
            for (int i = 0; i < keyPollutants.length; i++) {
                if (details[i] == max) keyPollutants[i]++;
            }
        }
        for (PollutantData pollutant : PollutionDetails.Companion.getPOLLUTANTS_DATA()) {
            var name = pollutant.getName();
            var count = keyPollutants[pollutant.getIndexInDetailsArray()];
            var percentage = calcPercentage(count, countOfIndexesOf2AndLarger);
            sb.append(String.format("<br>%s : %d (%s)", name, count, percentage));
        }
        return sb.toString();
    }

    /*
     * Generates a list of clickable info on stations, including:
     * stationId, indexLevel, details of pollution.
     * Details of pollution are shown as encoded PollutionDetails, each digit is an index level for consecutive
     * pollutant. 9 - stands for unknown. There are 7 pollutants monitored.
     *
     * Each row is a link which leads to Google maps with latitude and longitude of it's station as parameters.
     * Example output:
     *
     * Data from individual stations (click at a station to see it on a map)
     * 14: 0  (990099)
     * 16: 0  (990099)
     * 38: 0  (990099)
     * 52: 0  (900000)
     * 70: 0  (990099)
     * 74: 0  (9900099)
     * 84: 0  (9909999)
     * 109: 1  (1900000)
     *
     */
    private static String getListOfStationsAsLinks(List<AirQualityLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br><br><hr>Data from individual stations (click at a station to see it on a map)");
        for (AirQualityLog log : logs) {
            var id = log.getStationId();
            var index = log.getIndexLevel();
            var details = log.getDetails().encode();
            var location = log.getLocation();
            var mapAddress = String.format(Locale.US,
                    "https://www.google.com/maps/search/?api=1&query=%f,%f",
                    location.getLatitude(),
                    location.getLongitude());

            sb.append(String.format("<br> <a href = %s>%d: <b>%d</b> &nbsp;(%d)</a>", mapAddress, id, index, details));
        }
        return sb.toString();
    }

    private static String calcPercentage(int numerator, int denominator) {
        if (denominator == 0) return "[div by zero]";
        var result = (int) (100 * (numerator / (double) denominator));
        return String.format("%d%%", result);
    }
}
