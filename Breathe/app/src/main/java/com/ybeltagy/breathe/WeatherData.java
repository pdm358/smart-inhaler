
package com.ybeltagy.breathe;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 6 state level (used for pollen indexes) + 1 state for NULL
 */
enum Level {
    NONE,
    VERY_LOW,
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH,
    NULL
}

/**
 * Encapsulates area weather conditions (data from ClimaCell API) at the time of a given InhalerUsageEvent
 * - One InhalerUsageEvent object contains one WeatherData object
 * <p>
 */
public class WeatherData {

    private double weatherTemperature; // Celsius
    private double weatherHumidity; // percentage out of 100
    private int weatherEPAIndex; // Air quality index, as defined by the EPA
    private double weatherPrecipitationIntensity; // mm/hr
    private Level weatherTreeIndex; // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherGrassIndex; // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }

    //fixme: extract to secret file later.
    //todo: use volley
    //todo: refactor
    //todo: use endtime.
    //todo: better way to build the string.
    //fixme: handle errors and exceptions.
    //todo: comment
    //todo: error handling for the UX
    //todo: make calendar use UTC
    //todo: Use a broadcast receiver.
    public static String apiKey;

    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String EPAINDEX = "epaIndex";
    public static final String PRECIPITATIONINTENSITY = "precipitationIntensity";
    public static final String TREEINDEX = "treeIndex";
    public static final String GRASSINDEX = "grassIndex";

    /**
     *
     * @param calendar
     * @return
     */
    public static String getTimestampISO8601(Calendar calendar){
        Date date = calendar.getTime();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     *
     * @return
     */
    public static WeatherData syncGetWeatherData(Calendar startTime, double latitude, double longitude){

        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.SECOND, 60);

        String url = "https://data.climacell.co/v4/timelines?location=" + latitude + "," + longitude +
                "&fields=" + TEMPERATURE + "," + HUMIDITY + "," + EPAINDEX + "," + PRECIPITATIONINTENSITY + "," + TREEINDEX + "," + GRASSINDEX +
                "&startTime=" + getTimestampISO8601(startTime) +
                "&endTime=" + getTimestampISO8601(endTime) +
                "&timesteps=1m" +
                "&apikey=" + apiKey;

        Log.d("WeatherData", url);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d("WeatherData", request.toString());

        try (Response response = client.newCall(request).execute()) {
            JSONObject obj = new JSONObject(response.body().string()).getJSONObject("data");
            obj = (obj.getJSONArray("timelines").getJSONObject(0)).getJSONArray("intervals").getJSONObject(0).getJSONObject("values");

            WeatherData weatherData = new WeatherData();

            weatherData.weatherTemperature = obj.getDouble(TEMPERATURE);
            Log.d("WeatherData", "Temperature : " + weatherData.weatherTemperature);
            weatherData.weatherHumidity = obj.getDouble(HUMIDITY);
            Log.d("WeatherData", "Humidity : " + weatherData.weatherHumidity);
            weatherData.weatherEPAIndex = obj.getInt(EPAINDEX);
            Log.d("WeatherData", "EPA Index : " + weatherData.weatherEPAIndex);
            weatherData.weatherPrecipitationIntensity = obj.getDouble(PRECIPITATIONINTENSITY); //fixme: not sure if double or int
            Log.d("WeatherData",
                    "Precipitation Intensity : " + weatherData.weatherPrecipitationIntensity);
            weatherData.weatherGrassIndex = Level.values()[obj.getInt(GRASSINDEX)];
            Log.d("WeatherData", "Grass Index : " + weatherData.weatherGrassIndex);
            weatherData.weatherTreeIndex = Level.values()[obj.getInt(TREEINDEX)];
            Log.d("WeatherData", "Tree Index : " + weatherData.weatherTreeIndex);
            return weatherData;
        }catch(IOException e){
            return null;
        }catch(JSONException e){
            return null;
        }

    }

    //todo: implement a toString method.

    public WeatherData(double weatherTemperature, double weatherHumidity,
                       double weatherPrecipitationIntensity,
                       Level weatherGrassIndex, Level weatherTreeIndex,
                       int weatherEPAIndex) {
        setWeatherTemperature(weatherTemperature);
        setWeatherHumidity(weatherHumidity);
        setWeatherPrecipitationIntensity(weatherPrecipitationIntensity);
        setWeatherGrassIndex(weatherGrassIndex);
        setWeatherTreeIndex(weatherTreeIndex);
        setWeatherEPAIndex(weatherEPAIndex);
    }

    public WeatherData() {
        setWeatherTemperature(-1);
        setWeatherHumidity(-1);
        setWeatherPrecipitationIntensity(-1.0);
        setWeatherGrassIndex(Level.NULL);
        setWeatherTreeIndex(Level.NULL);
        setWeatherEPAIndex(-1);
    }

    public double getWeatherTemperature() {
        return weatherTemperature;
    }

    public void setWeatherTemperature(double weatherTemperature) {
        this.weatherTemperature = weatherTemperature;
    }

    public double getWeatherHumidity() {
        return weatherHumidity;
    }

    public void setWeatherHumidity(double weatherHumidity) {
        this.weatherHumidity = weatherHumidity;
    }

    public int getWeatherEPAIndex() {
        return weatherEPAIndex;
    }

    public void setWeatherEPAIndex(int weatherEPAIndex) {
        this.weatherEPAIndex = weatherEPAIndex;
    }

    public double getWeatherPrecipitationIntensity() {
        return weatherPrecipitationIntensity;
    }

    public void setWeatherPrecipitationIntensity(double weatherPrecipitationIntensity) {
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
