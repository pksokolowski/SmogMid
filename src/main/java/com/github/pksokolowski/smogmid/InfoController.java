package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.api.StationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class InfoController {

    @RequestMapping("/info")
    public String getInfo(){

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StationModel[]> responseEntity = restTemplate.getForEntity("http://api.gios.gov.pl/pjp-api/rest/station/findAll", StationModel[].class);

        if(responseEntity == null) return "null response";
        final var body = responseEntity.getBody();
        if(body == null) return "null body";

        List<StationModel> stations = Arrays.asList(body);

        return stations.toString();
    }
}
