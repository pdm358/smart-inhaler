package com.ybeltagy.breathe;
import com.ybeltagy.breathe.weather_data_collection.CollectWeatherData;

import java.time.Instant;
import java.util.Random;

import org.junit.Test;


import static org.junit.Assert.*;

public class WeatherDataTest {
    //todo: test future and past requests.

    // TODO: For some reason, these tests do not run/work for me although I see the API calls
    //       working in the Log files when I run the app. Possibly to do with the API key being
    //       initialized in the main activity

    @Test
    public void syncGetCurrentWeatherDataForSeattle() {
        assertNotNull(CollectWeatherData.syncGetWeatherDataJSONString(Instant.now(),47.6062, -122.3321));
    }

    @Test
    public void syncGetCurrentWeatherDataForSpokane() {
        assertNotNull(CollectWeatherData.syncGetWeatherDataJSONString(Instant.now(),47.6588, -117.4260));
    }

    @Test
    public void syncGetCurrentWeatherDataForMiami() {
        assertNotNull(CollectWeatherData.syncGetWeatherDataJSONString(Instant.now(),25.761681, -80.191788));
    }

    @Test
    public void syncGetCurrentWeatherDataForRandomUSCoordinate() {
        double USLatCenter = 39.8283; // The Lat and Lng of a point in the center of the US.
        double USLngCenter = -98.5795;
        double USLatRange = 16;
        double USLngRange = 44;
        Random r = new Random();

        // Get a random position in the US assuming the US is a rectangle.
        // Not a uniform distribution though.
        assertNotNull(CollectWeatherData.syncGetWeatherDataJSONString(Instant.now(),
                USLatCenter + (r.nextDouble()-0.5) * USLatRange,
                USLngCenter + (r.nextDouble()-0.5) * USLngRange));
    }

}