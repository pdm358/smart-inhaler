package com.ybeltagy.breathe.data;

import androidx.room.Ignore;

/**
 * Encapsulates area weather conditions (data from ClimaCell API) at the time of a given InhalerUsageEvent
 * - One InhalerUsageEvent object contains one WeatherData object
 * <p>
 * TODO: write static WeatherData createWeatherData() to gather conditions from ClimaCell API
 * and create a WeatherData object
 */
public class WeatherData {

    private float weatherTemperature; // Celsius
    private float weatherHumidity; // percentage out of 100 - value should always be =< 1
    private Level weatherPollen; // LOW, MEDIUM, or HIGH
    private int weatherAQI; // Air quality index, as defined by the EPA

    public WeatherData(){
        this(DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_LEVEL,
                DataFinals.DEFAULT_INTEGER);
    }

    @Ignore
    public WeatherData(float weatherTemperature, float weatherHumidity,
                       Level weatherPollen, int weatherAQI) {
        setWeatherTemperature(weatherTemperature);
        setWeatherHumidity(weatherHumidity);
        setWeatherPollen(weatherPollen);
        setWeatherAQI(weatherAQI);
    }

    /**
     *
     * @return true if all the data members are different from DataFinal default values.
     */
    public boolean isDataValid(){
        return isWeatherTemperatureValid() &&
                isWeatherHumidityValid() &&
                isWeatherPollenValid() &&
                isWeatherAQIValid();

    }

    public boolean isWeatherTemperatureValid(){
        return weatherTemperature != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isWeatherHumidityValid(){
        return weatherHumidity != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isWeatherPollenValid(){
        return weatherPollen != DataFinals.DEFAULT_LEVEL;
    }

    public boolean isWeatherAQIValid(){
        return weatherAQI != DataFinals.DEFAULT_INTEGER;
    }

    public float getWeatherTemperature() {
        return weatherTemperature;
    }

    public void setWeatherTemperature(float weatherTemperature) {
        this.weatherTemperature = DataUtilities.nanGuard(weatherTemperature);
    }

    public float getWeatherHumidity() {
        return weatherHumidity;
    }

    public void setWeatherHumidity(float weatherHumidity) {
        this.weatherHumidity = DataUtilities.nanGuard(weatherHumidity);
    }

    public Level getWeatherPollen() {
        return weatherPollen;
    }

    public void setWeatherPollen(Level weatherPollen) {
        this.weatherPollen = weatherPollen;
    }

    public int getWeatherAQI() {
        return weatherAQI;
    }

    public void setWeatherAQI(int weatherAQI) {
        this.weatherAQI = weatherAQI;
    }
}
