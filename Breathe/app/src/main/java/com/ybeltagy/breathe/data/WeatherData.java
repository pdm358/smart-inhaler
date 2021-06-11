package com.ybeltagy.breathe.data;

import androidx.room.Ignore;

/**
 * Encapsulates area weather conditions (data from tomorrow.io API) at the time of a given InhalerUsageEvent
 * - One InhalerUsageEvent object contains one WeatherData object
 * <p>
 */
public class WeatherData {

    //todo: use java docs. Remove hard examples.

    private float weatherTemperature; // Celsius

    private float weatherHumidity; // ratio out of 1 - value should always be =< 1
    private float weatherPrecipitationIntensity; // mm/hr

    // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherTreeIndex;
    // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherGrassIndex;

    // Air quality index, as defined by the EPA
    private int weatherEPAIndex;

    public WeatherData(){
        this(DataFinals.DEFAULT_FLOAT,       // temperature
                DataFinals.DEFAULT_FLOAT,    // humidity
                DataFinals.DEFAULT_FLOAT,    // precipitation
                DataFinals.DEFAULT_LEVEL,    // tree pollen
                DataFinals.DEFAULT_LEVEL,    // grass pollen
                DataFinals.DEFAULT_INTEGER); // epaIndex (also known as AQI)
    }

    @Ignore
    public WeatherData(float temperature, float humidity, float precipitationIntensity,
                       Level treePollen, Level grassPollen, int epaIndex) {
        setWeatherTemperature(temperature);
        setWeatherHumidity(humidity);
        setWeatherPrecipitationIntensity(precipitationIntensity);
        setWeatherTreeIndex(treePollen);
        setWeatherGrassIndex(grassPollen);
        setWeatherEPAIndex(epaIndex);
    }

    /**
     *
     * @return true if all the data members are different from DataFinal default values.
     */
    public boolean isDataValid(){
        return isWeatherTemperatureValid() &&
                isWeatherHumidityValid() &&
                isWeatherPrecipitationIntensityValid() &&
                isWeatherTreeIndexValid() &&
                isWeatherGrassIndexValid() &&
                isWeatherEPAIndexValid();

    }

    public boolean isWeatherTemperatureValid(){
        return weatherTemperature != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isWeatherHumidityValid(){
        return weatherHumidity != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isWeatherPrecipitationIntensityValid(){
        return weatherPrecipitationIntensity != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isWeatherTreeIndexValid(){
        return weatherTreeIndex != DataFinals.DEFAULT_LEVEL;
    }

    public boolean isWeatherGrassIndexValid(){
        return weatherGrassIndex != DataFinals.DEFAULT_LEVEL;
    }

    public boolean isWeatherEPAIndexValid(){
        return weatherEPAIndex != DataFinals.DEFAULT_INTEGER;
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

    public float getWeatherPrecipitationIntensity() {
        return weatherPrecipitationIntensity;
    }

    public void setWeatherPrecipitationIntensity(float weatherPrecipitationIntensity) {
        this.weatherPrecipitationIntensity = DataUtilities.nanGuard(weatherPrecipitationIntensity);
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

    public int getWeatherEPAIndex() {
        return weatherEPAIndex;
    }

    public void setWeatherEPAIndex(int weatherEPAIndex) {
        this.weatherEPAIndex = weatherEPAIndex;
    }
}
