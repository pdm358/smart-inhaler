package com.ybeltagy.breathe;
import java.util.Calendar;
import java.util.Random;

import org.junit.Test;


import static org.junit.Assert.*;

public class WeatherDataTest {
    //todo: test future and past requests.

    @Test
    public void syncGetCurrentWeatherDataForSeattle() {
        assertNotNull(WeatherData.syncGetWeatherData(Calendar.getInstance(),47.6062, -122.3321));
    }

    @Test
    public void syncGetCurrentWeatherDataForSpokane() {
        assertNotNull(WeatherData.syncGetWeatherData(Calendar.getInstance(),47.6588, -117.4260));
    }

    @Test
    public void syncGetCurrentWeatherDataForMiami() {
        assertNotNull(WeatherData.syncGetWeatherData(Calendar.getInstance(),25.761681, -80.191788));
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
        assertNotNull(WeatherData.syncGetWeatherData(Calendar.getInstance(), USLatCenter + (r.nextDouble()-0.5) * USLatRange, USLngCenter + (r.nextDouble()-0.5) * USLngRange));
    }

}