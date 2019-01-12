package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import com.github.pksokolowski.smogmid.db.PollutionDetails;
import com.github.pksokolowski.smogmid.repository.AirQualityLogsRepository;
import com.github.pksokolowski.smogmid.utils.LatLng;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AirQualityControllerTest {

    @Mock
    AirQualityLogsRepository mockRepository;

    @InjectMocks
    private AirQualityController controller;

    @Test
    public void prefersNearestStation(){
        var data = Arrays.asList(
                new AirQualityLog(100, 1, new PollutionDetails(1111111), new LatLng(50.001, 20.001)),
                new AirQualityLog(200, 2, new PollutionDetails(2222222), new LatLng(50.002, 20.002))
        );
        when(mockRepository.findAll()).thenReturn(data);

        var response = controller.getAQIndex(50.0, 20.0);
        var expected = new AQResponse(1, 1,1,1,1,1,1,1);
        assertEquals(expected, response);
    }

    @Test
    public void returnsNoIndexWhenNoDataIsAvailableForTheRequestedLocation(){
        var data = Collections.singletonList(
                new AirQualityLog(100, 1, new PollutionDetails(1111111), new LatLng(60.001, 30.001))
        );
        when(mockRepository.findAll()).thenReturn(data);

        var response = controller.getAQIndex(50.0, 20.0);
        var expected = new AQResponse(-1, -1,-1,-1,-1,-1,-1,-1);
        assertEquals(expected, response);
    }
}
