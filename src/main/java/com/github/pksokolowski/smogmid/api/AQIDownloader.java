package com.github.pksokolowski.smogmid.api;

import com.github.pksokolowski.smogmid.api.models.AQIModel;
import com.github.pksokolowski.smogmid.api.models.StationModel;
import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.utils.AQIDataConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant to consume the API of the Polish Inspectorate of Environmental Protection.
 * It downloads indexes for all existing stations, the stations are provided by a dependency.
 */
@Component
public class AQIDownloader {

    private RestTemplate restTemplate;
    private StationsDownloader stationsDownloader;

    public AQIDownloader(RestTemplate restTemplate, StationsDownloader stationsDownloader){
        this.restTemplate = restTemplate;
        this.stationsDownloader = stationsDownloader;
    }

    public List<AirQualityLog> getAirQualityLogs(){
        final var stations = stationsDownloader.getAllStations();
        final var AQLogs = new ArrayList<AirQualityLog>();

        for(StationModel s : stations){
            final var AQI = restTemplate.getForObject("https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/{stationId}", AQIModel.class, s.getId());
            if(AQI == null) continue;

            final var log = AQIDataConverter.Companion.toAirQualityLog(AQI, s);
            AQLogs.add(log);
        }

        return AQLogs;
    }
}
