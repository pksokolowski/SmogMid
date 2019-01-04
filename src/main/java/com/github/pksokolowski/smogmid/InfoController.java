package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.db.PollutionDetails;
import com.github.pksokolowski.smogmid.db.PollutionDetails.PollutantData;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import com.github.pksokolowski.smogmid.repository.UpdateLogsRepository;
import com.github.pksokolowski.smogmid.utils.TimeHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        StringBuilder sb = new StringBuilder();
        sb.append("<body bgcolor=\"#000000\" text=\"white\" link=\"white\" vlink=\"beige\"/>");
        sb.append("Info page");
        sb.append("<br>Logs count: ").append(logs.size());

        // get air quality index levels distribution
        var aqIndexDistribution = new int[]{0, 0, 0, 0, 0, 0};
        var countWithIndex = 0;
        for (AirQualityLog log : logs) {
            var indexLevel = log.getIndexLevel();
            if (indexLevel == -1) continue;
            countWithIndex++;
            aqIndexDistribution[indexLevel]++;
        }

        sb.append("<br>Logs with indexes: ").append(countWithIndex);

        if (updateLog != null) {
            final var updateTime = TimeHelper.getDateTimeStampString(updateLog.getTimeStamp());
            sb.append("<br>Update time: ").append(updateTime);
            sb.append("<br>Update duration in millis: ").append(updateLog.getDuration());
        }

        sb.append("<br><br>Percentages of specific index levels:");
        if (countWithIndex > 0) {
            for (int i = 0; i < aqIndexDistribution.length; i++) {
                int count = aqIndexDistribution[i];
                var percentage = calcPercentage(count, countWithIndex);
                sb.append(String.format("<br> %d : %s", i, percentage));
            }
        }

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

    private String calcPercentage(int numerator, int denominator) {
        if(denominator == 0) return "[div by zero]";
        var result = (int) (100 * (numerator / (double) denominator));
        return String.format("%d%%", result);
    }
}
