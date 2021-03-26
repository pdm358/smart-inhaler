package com.ybeltagy.breathe;

// date-tme with an offset from UTC/Greenwich in the ISO-8601 calendar system

import java.time.OffsetDateTime;

/**
 * Entity (used in Room database) that represents an inhaler usage event.  Contains:
 * - a unique UTC ISO-8601 timestamp (when it occurred)
 * <p>
 * - a WearableData object: environmental conditions from smart pin/wearable at the time of the IUE
 * , if the information is available
 * <p>
 * - a DiaryEntry object: user comments and tags for the IUE, if the info is available
 * <p>
 * - a WeatherData object: weather data from the Climacell API at the time of the IUE, if the
 * information is available
 * <p>
 * TODO: create static IUE createIUE() to collect all data for IUE and return an IUE object
 */
public class IUE {
    private OffsetDateTime timeStamp;
    private WeatherData weatherData;
    private DiaryEntry diaryEntry;
    private WearableData wearableData;

    public IUE(OffsetDateTime timeStamp, WeatherData weatherData, DiaryEntry diaryEntry, WearableData wearableData) {
        this.timeStamp = timeStamp;
        this.weatherData = weatherData;
        this.diaryEntry = diaryEntry;
        this.wearableData = wearableData;
    }

    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    public DiaryEntry getDiaryEntry() {
        return diaryEntry;
    }

    public void setDiaryEntry(DiaryEntry diaryEntry) {
        this.diaryEntry = diaryEntry;
    }

    public WearableData getWearableData() {
        return wearableData;
    }

    public void setWearableData(WearableData wearableData) {
        this.wearableData = wearableData;
    }
}
