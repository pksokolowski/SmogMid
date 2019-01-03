package com.github.pksokolowski.smogmid.api;

import com.github.pksokolowski.smogmid.api.models.StationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  This class is meant to consume the API of the Polish Inspectorate of Environmental Protection.
 *  It downloads a list of all active measurement stations.
 */
@Component
public class StationsDownloader {
    private RestTemplate restTemplate;

    public StationsDownloader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StationModel> getAllStations() {
        ResponseEntity<StationModel[]> responseEntity = restTemplate.getForEntity("http://api.gios.gov.pl/pjp-api/rest/station/findAll", StationModel[].class);

        if (responseEntity == null) return new ArrayList<>();
        final var body = responseEntity.getBody();
        if (body == null) return new ArrayList<>();

        return Arrays.asList(body);
    }
}
