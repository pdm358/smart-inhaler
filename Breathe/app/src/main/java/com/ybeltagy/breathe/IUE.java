package com.ybeltagy.breathe;

// date-tme with an offset from UTC/Greenwich in the ISO-8601 calendar system

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.OffsetDateTime;

/**
 * Entity (used in Room database) that represents an inhaler usage event.  Contains:
 * - a unique UTC ISO-8601 timestamp (when it occurred)
 * <p>
 * The following objects are "embedded" (represents an object we would like to decompose into its
 * sub-fields within a table) ->
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
@Entity(tableName = "IUE_table")
public class IUE {
    @PrimaryKey // timeStamp is the unique identifier for each IUE record
    @NonNull // this can never be null
    @ColumnInfo(name = "UTC_ISO_8601_date_time") // name of the column in Room that stores timeStamp
    private OffsetDateTime timeStamp;

    // I made these public because all of the Embedded examples I saw were public
    // For more info on Embedded objects within Entities,
    // see https://developer.android.com/training/data-storage/room/relationships
    @Embedded public WeatherData weatherData;
    @Embedded public DiaryEntry diaryEntry;
    @Embedded public WearableData wearableData;

    // @NonNull annotation means timeStamp parameter can never be null
    public IUE(@NonNull OffsetDateTime timeStamp, WeatherData weatherData,
               DiaryEntry diaryEntry, WearableData wearableData) {
        this.timeStamp = timeStamp;
        this.weatherData = weatherData;
        this.diaryEntry = diaryEntry;
        this.wearableData = wearableData;
    }

    // Note: getter methods are required by Room so it can instantiate IUE objects
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
