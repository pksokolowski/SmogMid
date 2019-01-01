package com.github.pksokolowski.smogmid.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirQualityController {

    // notice the last slash character, it is there to prevent Spring from truncating the double value of the last
    // parameter, as it is expected to have a dot in it, the part after the dot would otherwise be truncated.
    @RequestMapping("/getIndex/{lat}/{lng}/")
    public String getAQIndex(
            @PathVariable("lat") Double latitude,
            @PathVariable("lng") Double longitude) {
        return latitude.toString()+" "+longitude.toString();
    }

}
