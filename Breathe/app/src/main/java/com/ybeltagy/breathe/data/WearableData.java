package com.ybeltagy.breathe.data;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.Instant;

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
    private Instant wearableDataTimeStamp; // when this wearableData was collected

    private float temperature; // The default null values should not make sense.
    private float humidity; //fixme: these variable names are asymmetric compared to weather data.
    private char character; // they are also confusing.
    private char digit;

    /**
     * Added this constructor for our iteration.
     * Perhaps in the future, the smart pin will send a timestamp with its data but for our current
     * team, this is not necessary.
     */
    @SuppressLint("NewApi")
    @Ignore
    public WearableData() {

        this(Instant.now(),
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_CHAR,
                DataFinals.DEFAULT_CHAR);

    }

    public WearableData(@NonNull Instant wearableDataTimeStamp) {
        this(wearableDataTimeStamp,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_FLOAT,
                DataFinals.DEFAULT_CHAR,
                DataFinals.DEFAULT_CHAR);
    }

    @Ignore
    public WearableData(@NonNull Instant wearableDataTimeStamp,
                        float temperature,
                        float humidity,
                        char character,
                        char digit){

        setWearableDataTimeStamp(wearableDataTimeStamp);
        setTemperature(temperature);
        setHumidity(humidity);
        setCharacter(character);
        setDigit(digit);
    }

    /**
     * @return true if all the the data members are different from DataFinal default values.
     */
    public boolean isDataValid(){
        return isTemperatureValid() &&
                isHumidityValid() &&
                isCharacterValid() &&
                isDigitValid();
    }

    public boolean isTemperatureValid(){
        return temperature != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isHumidityValid(){
        return humidity != DataFinals.DEFAULT_FLOAT;
    }

    public boolean isCharacterValid(){
        return character != DataFinals.DEFAULT_CHAR;
    }

    public boolean isDigitValid(){
        return digit != DataFinals.DEFAULT_CHAR;
    }

    @NonNull
    public Instant getWearableDataTimeStamp() {
        return wearableDataTimeStamp;
    }

    public void setWearableDataTimeStamp(@NonNull Instant wearableTimeStamp) {
        this.wearableDataTimeStamp = wearableTimeStamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public char getDigit() {
        return digit;
    }

    public void setDigit(char digit) {
        this.digit = digit;
    }
}
