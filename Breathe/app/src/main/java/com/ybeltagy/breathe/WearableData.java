package com.ybeltagy.breathe;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.OffsetDateTime;

/**
 * Encapsulates local environmental data gathered from the user's smart pin/wearable
 * - uses wearableDataTimeStamp (from when it was collected) as its primary key
 * - currently only contains one actual field of data (temperature)
 * - the other placeholder fields will be used by future teams when additional sensors are added to
 * to the smart pin/wearable
 * <p>
 * TODO: write static WearableData createWearableData() to collect smart pin data
 * and create a WearableData object
 */
@Entity(tableName = "WearableData_table")
public class WearableData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Wearable_Data_UTC_ISO_8601_date_time")
    private OffsetDateTime wearableDataTimeStamp; // when this wearableData was collected

    private int wearableTemperature = 0;
    private int placeHolder1 = 0;
    private int placeHolder2 = 0;
    private int placeHolder3 = 0;

    /**
     * Added this constructor for our iteration.
     * Perhaps in the future, the smart pin will send a timestamp with its data but for our current
     * team, this is not necessary.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public WearableData() {
        wearableDataTimeStamp = OffsetDateTime.now();
    }

    public WearableData(@NonNull OffsetDateTime wearableDataTimeStamp) {
        this.wearableDataTimeStamp = wearableDataTimeStamp;
    }

    @NonNull
    public OffsetDateTime getWearableDataTimeStamp() {
        return wearableDataTimeStamp;
    }

    public void setWearableDataTimeStamp(@NonNull OffsetDateTime wearableTimeStamp) {
        this.wearableDataTimeStamp = wearableTimeStamp;
    }

    public int getWearableTemperature() {
        return wearableTemperature;
    }

    public void setWearableTemperature(int wearableTemperature) {
        this.wearableTemperature = wearableTemperature;
    }

    public int getPlaceHolder1() {
        return placeHolder1;
    }

    public void setPlaceHolder1(int placeHolder1) {
        this.placeHolder1 = placeHolder1;
    }

    public int getPlaceHolder2() {
        return placeHolder2;
    }

    public void setPlaceHolder2(int placeHolder2) {
        this.placeHolder2 = placeHolder2;
    }

    public int getPlaceHolder3() {
        return placeHolder3;
    }

    public void setPlaceHolder3(int placeHolder3) {
        this.placeHolder3 = placeHolder3;
    }
}
