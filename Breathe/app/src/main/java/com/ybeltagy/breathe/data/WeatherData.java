package com.ybeltagy.breathe.data;

import androidx.room.Ignore;

/**
 * Encapsulates area weather conditions (data from tomorrow.io API) at the time of a given InhalerUsageEvent
 * - One InhalerUsageEvent object contains one WeatherData object
 * <p>
 */
public class WeatherData {

    private float weatherTemperature = DataFinals.DEFAULT_FLOAT; // Celsius
    private float weatherHumidity = DataFinals.DEFAULT_FLOAT; // percentage out of 100 - value should always be =< 1
    private float weatherPrecipitationIntensity; // mm/hr

    // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherTreeIndex = DataFinals.DEFAULT_LEVEL;
    // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherGrassIndex= DataFinals.DEFAULT_LEVEL;

    // Air quality index, as defined by the EPA
    private int weatherEPAIndex = DataFinals.DEFAULT_INTEGER;

    public WeatherData(){}

    @Ignore
    public WeatherData(float temperature, float humidity, float precipitationIntensity,
                       Level grassPollen, Level treePollen, int aQI) {
        setWeatherTemperature(temperature);
        setWeatherHumidity(humidity);
        setWeatherPrecipitationIntensity(precipitationIntensity);
        setWeatherGrassIndex(grassPollen);
        setWeatherTreeIndex(treePollen);
        setWeatherEPAIndex(aQI);
    }


    //todo: implement a toString method.

    public float getWeatherTemperature() {
        return weatherTemperature;
    }

    public void setWeatherTemperature(float weatherTemperature) {
        this.weatherTemperature = weatherTemperature;
    }

    public float getWeatherHumidity() {
        return weatherHumidity;
    }

    public void setWeatherHumidity(float weatherHumidity) {
        this.weatherHumidity = weatherHumidity;
    }

    public int getWeatherEPAIndex() {
        return weatherEPAIndex;
    }

    public void setWeatherEPAIndex(int weatherEPAIndex) {
        this.weatherEPAIndex = weatherEPAIndex;
    }

    public float getWeatherPrecipitationIntensity() {
        return weatherPrecipitationIntensity;
    }

    public void setWeatherPrecipitationIntensity(float weatherPrecipitationIntensity) {
        this.weatherPrecipitationIntensity = weatherPrecipitationIntensity;
    }

    public Level getWeatherTreeIndex() {
        return weatherTreeIndex;
    }

    public void setWeatherTreeIndex(Level weatherTreeIndex) {
        this.weatherTreeIndex = weatherTreeIndex;
    }

    public Level getWeatherGrassIndex() {
        return weatherGrassIndex;
    }

    public void setWeatherGrassIndex(Level weatherGrassIndex) {
        this.weatherGrassIndex = weatherGrassIndex;
    }
}
