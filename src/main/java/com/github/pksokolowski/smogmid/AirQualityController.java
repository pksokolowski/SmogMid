package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.db.PollutionDetails;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import com.github.pksokolowski.smogmid.utils.DistanceHelper;
import com.github.pksokolowski.smogmid.utils.LatLng;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.pksokolowski.smogmid.db.PollutionDetails.*;

@RestController
public class AirQualityController {

    private final int ACCEPTABLE_DISTANCE_KILOMETERS = 10;

    private AirQualityLogsRepository aqLogsRepository;

    public AirQualityController(AirQualityLogsRepository aqLogsRepository) {
        this.aqLogsRepository = aqLogsRepository;
    }

    // notice the last slash character, it is there to prevent Spring from truncating the double value of the last
    // parameter, as it is expected to have a dot in it, the part after the dot would otherwise be truncated.
    @RequestMapping("/getIndex/{lat}/{lng}/")
    public AQResponse getAQIndex(
            @PathVariable("lat") Double latitude,
            @PathVariable("lng") Double longitude) {

        final var userLocation = new LatLng(latitude, longitude);

        Function<AirQualityLog, Boolean> isClose = log -> {
            final var location = log.getLocation();
            final var distance = DistanceHelper.distance(location, userLocation);
            return !(distance > ACCEPTABLE_DISTANCE_KILOMETERS);
        };

        Comparator<AirQualityLog> comparator = Comparator.comparing(
                (AirQualityLog o) -> DistanceHelper.distance(userLocation, o.getLocation())
        );

        var logs = aqLogsRepository.findAll().stream()
                .filter(isClose::apply)
                .sorted(comparator)
                .collect(Collectors.toList());

        // assemble the response
        PollutionDetails details = new PollutionDetails();
        for (AirQualityLog log : logs) {
            details = details.combinedWith(log.getDetails());
        }

        var subIndexes = details.getDetailsArray();

        return new AQResponse(details.getHighestIndex(),
                subIndexes[SENSOR_INDEX_PM10],
                subIndexes[SENSOR_INDEX_PM25],
                subIndexes[SENSOR_INDEX_O3],
                subIndexes[SENSOR_INDEX_NO2],
                subIndexes[SENSOR_INDEX_SO2],
                subIndexes[SENSOR_INDEX_C6H6],
                subIndexes[SENSOR_INDEX_CO]
        );
    }

    @RequestMapping("/getAll")
    public Collection<AirQualityLog> getAllAirQualityLogs() {
        return new ArrayList<>(aqLogsRepository.findAll());
    }

}
