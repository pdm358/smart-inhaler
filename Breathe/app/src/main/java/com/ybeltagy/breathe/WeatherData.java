package com.ybeltagy.breathe;

enum Level {
    LOW,
    MEDIUM,
    HIGH
}

public class WeatherData {

    private double temperature; // Celsius
    private double humidity; // percentage out of 100 - value should always be =< 1
    private Level pollen; // LOW, MEDIUM, or HIGH
    private int aQI; // Air quality index, as defined by the EPA

    public WeatherData(double temperature, double humidity, Level pollen, int aQI) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pollen = pollen;
        this.aQI = aQI;
    }

    static WeatherData createWeatherData(double temperature, double humidity,
                                         Level pollen, int aQI) {
        return new WeatherData(temperature, humidity, pollen, aQI);
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public Level getPollen() {
        return pollen;
    }

    public void setPollen(Level pollen) {
        this.pollen = pollen;
    }

    public int getaQI() {
        return aQI;
    }

    public void setaQI(int aQI) {
        this.aQI = aQI;
    }
}
