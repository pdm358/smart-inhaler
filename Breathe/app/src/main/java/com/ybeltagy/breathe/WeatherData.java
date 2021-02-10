package com.ybeltagy.breathe;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    //fixme: double vs float
    private double temperature;

    private double humidity;

    private int epaIndex;

    private int precipitationIntensity; // fixme: not sure if double or int

    private int treeIndex;

    private int grassIndex;

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

        try (Response response = client.newCall(request).execute()) {
            JSONObject obj = new JSONObject(response.body().string()).getJSONObject("data");
            obj = (obj.getJSONArray("timelines").getJSONObject(0)).getJSONArray("intervals").getJSONObject(0).getJSONObject("values");

            WeatherData weatherData = new WeatherData();

            weatherData.temperature = obj.getDouble(TEMPERATURE);
            weatherData.humidity = obj.getDouble(HUMIDITY);
            weatherData.epaIndex = obj.getInt(EPAINDEX);
            weatherData.precipitationIntensity = obj.getInt(PRECIPITATIONINTENSITY); //fixme: not sure if double or int
            weatherData.grassIndex = obj.getInt(GRASSINDEX);
            weatherData.treeIndex = obj.getInt(TREEINDEX);
            return weatherData;
        }catch(IOException e){
            return null;
        }catch(JSONException e){
            return null;
        }

    }

    //todo: implement a toString method.
}
