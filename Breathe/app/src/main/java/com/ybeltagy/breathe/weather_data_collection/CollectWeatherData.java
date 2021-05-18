package com.ybeltagy.breathe.weather_data_collection;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ybeltagy.breathe.data.DataFinals;
import com.ybeltagy.breathe.data.Level;
import com.ybeltagy.breathe.data.WeatherData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A helper class to hold the Tomorrow.io api call code for the weather data
 */
public class CollectWeatherData {

    //fixme: extract to secret file later.
    //todo: use volley
    //todo: refactor
    //todo: use endtime.
    //todo: better way to build the string.
    //todo: error handling for the UX
    //todo: make calendar use UTC
    //todo: Use a broadcast receiver.
    protected static String apiKey;

    // API fields
    private static final String TEMPERATURE = "temperature";
    private static final String HUMIDITY = "humidity";
    private static final String EPAINDEX = "epaIndex";
    private static final String PRECIPITATIONINTENSITY = "precipitationIntensity";
    private static final String TREEINDEX = "treeIndex";
    private static final String GRASSINDEX = "grassIndex";

    /**
     * Get the ISO-8601 timestamp for right "now"
     * Note: this may be unnecessary - v4 api calls default to "now" for null start time
     *
     * @param calendar
     * @return String timestamp for current time
     */
    public static String getTimestampISO8601(Calendar calendar) {
        Date date = calendar.getTime();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        // Quoted "Z" to indicate UTC, no timezone offset
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     * Get weather data for the requested fields for right now from tomorrow.io
     *
     * @return WeatherData object for the current time and input latitude/longitude location
     */
    @SuppressLint("NewApi")
    public static String syncGetWeatherDataJSONString(Instant startTime,
                                                      double latitude, double longitude) {

        Instant endTime = startTime.plusSeconds(60);

        String url = "https://api.tomorrow.io/v4/timelines?location=" + latitude + "," + longitude +
                "&fields=" + TEMPERATURE + "," + HUMIDITY + "," + EPAINDEX + ","
                + PRECIPITATIONINTENSITY + "," + TREEINDEX + "," + GRASSINDEX +
                "&startTime=" + startTime.toString() + "&endTime=" + endTime.toString() +
                "&timesteps=1m" + "&apikey=" + apiKey;

        Log.d("WeatherData", url);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d("WeatherData", request.toString());

        try (Response response = client.newCall(request).execute()) {
            // FIXME: Add error checking
            return response.body().string();

        } catch (IOException e) {
            Log.e("WeatherData", "IOException : " + e.getMessage());
            return null;
        }
    }

    public static WeatherData responseJSONToWeatherData(String response) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(response).getJSONObject("data");

            obj = (obj.getJSONArray("timelines").getJSONObject(0))
                    .getJSONArray("intervals").getJSONObject(0)
                    .getJSONObject("values");

            WeatherData weatherData = new WeatherData();

            // get NaN if temperature doesn't exist (instead of throwing exception, which happened
            // to me a couple times)
            // https://stackoverflow.com/questions/15477304/android-jsonexception-no-value-for
            weatherData.setWeatherTemperature(
                    (float) obj.optDouble(TEMPERATURE, DataFinals.DEFAULT_FLOAT));
            Log.d("WeatherData", "Temperature : " + weatherData.getWeatherTemperature());

            weatherData.setWeatherHumidity(
                    (float) obj.optDouble(HUMIDITY, DataFinals.DEFAULT_FLOAT));
            Log.d("WeatherData", "Humidity : " + weatherData.getWeatherHumidity());

            weatherData.setWeatherEPAIndex(obj.optInt(EPAINDEX, DataFinals.DEFAULT_INTEGER));
            Log.d("WeatherData", "EPA Index : " + weatherData.getWeatherEPAIndex());

            weatherData.setWeatherPrecipitationIntensity(
                    (float) obj.optDouble(PRECIPITATIONINTENSITY, DataFinals.DEFAULT_FLOAT));
            Log.d("WeatherData",
                    "Precipitation Intensity : "
                            + weatherData.getWeatherPrecipitationIntensity());

            weatherData.setWeatherTreeIndex(
                    Level.intToLevel(obj.optInt(TREEINDEX, DataFinals.DEFAULT_LEVEL.ordinal())));
            Log.d("WeatherData", "Tree Index : " + weatherData.getWeatherTreeIndex()
                    + " = " + weatherData.getWeatherTreeIndex().ordinal());

            weatherData.setWeatherGrassIndex(
                    Level.intToLevel(obj.optInt(GRASSINDEX, DataFinals.DEFAULT_LEVEL.ordinal())));
            Log.d("WeatherData", "Grass Index : " + weatherData.getWeatherGrassIndex()
                    + " = " + weatherData.getWeatherGrassIndex().ordinal());

            return weatherData;
        } catch (JSONException e) {
            Log.e("WeatherData", "JSONException : " + e.getMessage());
            return null;
        }
    }
}
