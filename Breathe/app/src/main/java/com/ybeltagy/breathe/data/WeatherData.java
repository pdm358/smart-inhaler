package com.ybeltagy.breathe.data;

import androidx.room.Ignore;
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
 * Encapsulates area weather conditions (data from ClimaCell API) at the time of a given InhalerUsageEvent
 * - One InhalerUsageEvent object contains one WeatherData object
 * <p>
 */
public class WeatherData {

    private float weatherTemperature = DataFinals.DEFAULT_FLOAT; // Celsius
    private float weatherHumidity = DataFinals.DEFAULT_FLOAT; // percentage out of 100 - value should always be =< 1
    private float weatherPrecipitationIntensity; // mm/hr

    // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherTreeIndex = DataFinals.DEFAULT_LEVEL;
    // { NONE,  VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL }
    private Level weatherGrassIndex= DataFinals.DEFAULT_LEVEL;

    // Air quality index, as defined by the EPA
    private int weatherEPAIndex = DataFinals.DEFAULT_INTEGER;

    public WeatherData(){}

    @Ignore
    public WeatherData(float temperature, float humidity, float precipitationIntensity,
                       Level grassPollen, Level treePollen, int aQI) {
        setWeatherTemperature(temperature);
        setWeatherHumidity(humidity);
        setWeatherPrecipitationIntensity(precipitationIntensity);
        setWeatherGrassIndex(grassPollen);
        setWeatherTreeIndex(treePollen);
        setWeatherEPAIndex(aQI);
    }

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
        // FIXME: (after merge) change JSON_Object.getDouble(), .getInt(), etc. to .OptDouble(),
        //       .OptInt() and check for null.  If those values are null for the object, assign the
        //       WeatherData fields to the null constant values
        try (Response response = client.newCall(request).execute()) {
            JSONObject obj = new JSONObject(response.body().string()).getJSONObject("data");
            obj = (obj.getJSONArray("timelines").getJSONObject(0)).getJSONArray("intervals").getJSONObject(0).getJSONObject("values");

            WeatherData weatherData = new WeatherData();

            weatherData.weatherTemperature = (float) obj.getDouble(TEMPERATURE);
            Log.d("WeatherData", "Temperature : " + weatherData.weatherTemperature);

            weatherData.weatherHumidity = (float) obj.getDouble(HUMIDITY);
            Log.d("WeatherData", "Humidity : " + weatherData.weatherHumidity);

            weatherData.weatherEPAIndex = obj.getInt(EPAINDEX);
            Log.d("WeatherData", "EPA Index : " + weatherData.weatherEPAIndex);

            weatherData.weatherPrecipitationIntensity
                    = (float) obj.getDouble(PRECIPITATIONINTENSITY);
            Log.d("WeatherData",
                    "Precipitation Intensity : " + weatherData.weatherPrecipitationIntensity);

            weatherData.weatherGrassIndex = Level.values()[obj.getInt(GRASSINDEX)];
            Log.d("WeatherData", "Grass Index : " + weatherData.weatherGrassIndex);

            weatherData.weatherTreeIndex = Level.values()[obj.getInt(TREEINDEX)];
            Log.d("WeatherData", "Tree Index : " + weatherData.weatherTreeIndex);

            return weatherData;

        }catch(IOException e){
            Log.e("WeatherData", "IOException : " + e.getMessage());
            return null;
        }catch(JSONException e){
            Log.e("WeatherData", "JSONException : " + e.getMessage());
            return null;
        }

    }

    //todo: implement a toString method.

    public float getWeatherTemperature() {
        return weatherTemperature;
    }

    public void setWeatherTemperature(float weatherTemperature) {
        this.weatherTemperature = weatherTemperature;
    }

    public float getWeatherHumidity() {
        return weatherHumidity;
    }

    public void setWeatherHumidity(float weatherHumidity) {
        this.weatherHumidity = weatherHumidity;
    }

    public int getWeatherEPAIndex() {
        return weatherEPAIndex;
    }

    public void setWeatherEPAIndex(int weatherEPAIndex) {
        this.weatherEPAIndex = weatherEPAIndex;
    }

    public float getWeatherPrecipitationIntensity() {
        return weatherPrecipitationIntensity;
    }

    public void setWeatherPrecipitationIntensity(float weatherPrecipitationIntensity) {
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
