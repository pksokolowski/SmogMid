package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private AirQualityLogsRepository aqLogsRepository;

    public InfoController(AirQualityLogsRepository aqLogsRepository) {
        this.aqLogsRepository = aqLogsRepository;
    }

    @RequestMapping("/info")
    public String getInfo() {
        var logs = aqLogsRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("<body bgcolor=\"#000000\" text=\"white\"/>");
        sb.append("Info page");
        sb.append("<br>Logs count: ").append(logs.size());

        // get air quality index levels distribution
        var aqIndexDistribution = new int[]{0, 0, 0, 0, 0, 0};
        var countWithIndex = 0;
        for (AirQualityLog log : logs) {
            var indexLevel = log.getDetails().getHighestIndex();
            if (indexLevel == -1) continue;
            countWithIndex++;
            aqIndexDistribution[indexLevel]++;
        }

        sb.append("<br>Logs with data: ").append(countWithIndex);

        sb.append("<br><br>Percentages of specific index levels:");
        if (countWithIndex > 0) {
            for (int i = 0; i < aqIndexDistribution.length; i++) {
                int count = aqIndexDistribution[i];
                var percentage = (int) (100 * (count / (double) countWithIndex));
                sb.append(String.format("<br> %d : %d%%", i, percentage));
            }
        }
        sb.append("<br><br><hr>");
        for (AirQualityLog log : logs) {
            sb.append(log.getDetails().encode()).append(" , ");
        }

        return sb.toString();
    }

}
