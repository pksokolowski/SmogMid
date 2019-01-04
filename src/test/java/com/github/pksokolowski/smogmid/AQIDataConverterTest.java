package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.api.models.AQIModel;
import com.github.pksokolowski.smogmid.api.models.StationModel;
import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.db.PollutionDetails;
import com.github.pksokolowski.smogmid.utils.AQIDataConverter;
import com.github.pksokolowski.smogmid.utils.LatLng;
import org.junit.Test;

import java.util.function.Function;

import static com.github.pksokolowski.smogmid.db.PollutionDetails.*;
import static org.junit.Assert.assertEquals;

public class AQIDataConverterTest {
    @Test
    public void convertsSubIndexesInTheRightOrder() {
        var aqi = getModel(new PollutionDetails(1234509));
        var station = new StationModel(110, "50.000", "20.000");

        var result = AQIDataConverter.Companion.toAirQualityLog(aqi, station);
        var expected = new AirQualityLog(110, 5, new PollutionDetails(1234509), new LatLng(50.0, 20.0));

        assertEquals(expected, result);
    }

    @Test
    public void dealsWithNoDataCase() {
        var aqi = getModel(new PollutionDetails(9999999));
        var station = new StationModel(120, "50.000", "20.000");

        var result = AQIDataConverter.Companion.toAirQualityLog(aqi, station);
        var expected = new AirQualityLog(120, -1, new PollutionDetails(9999999), new LatLng(50.0, 20.0));

        assertEquals(expected, result);
    }

    @Test
    public void canHandleNullsInModel() {
        var aqi = new AQIModel(null, null, null, null, null, null, null, null);
        var station = new StationModel(600, "55.000", "25.000");

        var result = AQIDataConverter.Companion.toAirQualityLog(aqi, station);
        var expected = new AirQualityLog(600, -1, new PollutionDetails(9999999), new LatLng(55.0, 25.0));

        assertEquals(expected, result);
    }

    private AQIModel getModel(PollutionDetails details) {
        var pollutors = details.getDetailsArray();

        Function<Integer, AQIModel.IndexLevel> get = index -> getIndexLevel(pollutors[index]);

        return new AQIModel(getIndexLevel(details.getHighestIndex()),
                get.apply(SENSOR_INDEX_SO2),
                get.apply(SENSOR_INDEX_NO2),
                get.apply(SENSOR_INDEX_CO),
                get.apply(SENSOR_INDEX_PM10),
                get.apply(SENSOR_INDEX_PM25),
                get.apply(SENSOR_INDEX_O3),
                get.apply(SENSOR_INDEX_C6H6));
    }

    private AQIModel.IndexLevel getIndexLevel(int level) {
        return new AQIModel.IndexLevel(level);
    }
}
