package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

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
        sb.append("Info page<br>");
        sb.append("Logs count: ").append(logs.size());

        sb.append("<br><br>--------------<br>");
        for(AirQualityLog log : logs){
            sb.append(log.getDetails().encode()).append(" , ");
        }

        return sb.toString();
    }

    @RequestMapping("/getSaved")
    public Collection<AirQualityLog> getSavedLogs() {
        return new ArrayList<>(aqLogsRepository.findAll());
    }

}
