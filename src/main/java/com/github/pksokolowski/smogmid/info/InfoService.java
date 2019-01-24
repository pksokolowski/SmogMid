package com.github.pksokolowski.smogmid.info;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.db.PollutionDetails;
import com.github.pksokolowski.smogmid.db.PollutionDetails.PollutantData;
import com.github.pksokolowski.smogmid.db.UpdateLog;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import com.github.pksokolowski.smogmid.repository.UpdateLogsRepository;
import com.github.pksokolowski.smogmid.utils.TimeHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class InfoService {

    private AirQualityLogsRepository aqLogsRepository;
    private UpdateLogsRepository updateLogsRepository;

    public InfoService(AirQualityLogsRepository aqLogsRepository, UpdateLogsRepository updateLogsRepository) {
        this.aqLogsRepository = aqLogsRepository;
        this.updateLogsRepository = updateLogsRepository;
    }

    public Info getInfo(){
        var logs = aqLogsRepository.findAll();
        var updateLog = updateLogsRepository.findTopByOrderByTimeStampDesc();
        int countWithIndex = 0;
        for (AirQualityLog log : logs) {
            if (log.getIndexLevel() > -1) countWithIndex++;
        }

        return new Info(
                logs.size(),
                countWithIndex,
                obtainUpdateTime(updateLog),
                obtainUpdateDuration(updateLog),
                obtainIndexLevelDistribution(logs, countWithIndex),
                obtainKeyPollutantsBreakdown(logs),
                obtainDetails(logs)
        );
    }

    private String obtainUpdateTime(UpdateLog log){
        if (log == null) return "n/a";
            return TimeHelper.getDateTimeStampString(log.getTimeStamp());
    }

    private int obtainUpdateDuration(UpdateLog log){
        if (log == null) return -1;
        return (int)log.getDuration();
    }

    private List<Info.PercentageInfo> obtainIndexLevelDistribution(List<AirQualityLog> logs, int countWithIndex){
        var results = new ArrayList<Info.PercentageInfo>();
        var aqIndexDistribution = new int[]{0, 0, 0, 0, 0, 0};
        for (AirQualityLog log : logs) {
            var indexLevel = log.getIndexLevel();
            if (indexLevel == -1) continue;
            aqIndexDistribution[indexLevel]++;
        }

        if (countWithIndex > 0) {
            for (int i = 0; i < aqIndexDistribution.length; i++) {
                int count = aqIndexDistribution[i];
                var percentage = calcPercentage(count, countWithIndex);
                results.add(new Info.PercentageInfo(i, percentage));
            }
        }
        return results;
    }

    private List<Info.KeyPollutantsInfo> obtainKeyPollutantsBreakdown(List<AirQualityLog> logs){
        var results = new ArrayList<Info.KeyPollutantsInfo>();

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
            results.add(new Info.KeyPollutantsInfo(name, count, percentage));
        }
        return results;
    }

    private List<Info.DetailsInfo> obtainDetails(List<AirQualityLog> logs){
        var results = new ArrayList<Info.DetailsInfo>();
        for (AirQualityLog log : logs) {
            var id = log.getStationId();
            var index = log.getIndexLevel();
            var details = log.getDetails().encode();
            var location = log.getLocation();
            var mapAddress = String.format(Locale.US,
                    "https://www.google.com/maps/search/?api=1&query=%f,%f",
                    location.getLatitude(),
                    location.getLongitude());

            var linkText = String.format("%d: %d  (%d)", id, index, details);
            results.add(new Info.DetailsInfo(id, index, details, mapAddress));
        }
        return results;
    }

    private static int calcPercentage(int numerator, int denominator) {
        if (denominator == 0) return -1;
       return (int) (100 * (numerator / (double) denominator));
    }
}
