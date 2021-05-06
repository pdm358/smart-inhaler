package com.ybeltagy.breathe;

import android.graphics.Bitmap;

import com.google.gson.Gson;

/**
 * The Data class used for Work Manager I/O between Tasks can only take Boolean, Integer, Long,
 * Float, Double, and String. (https://developer.android.com/reference/androidx/work/Data.Builder)
 *
 * To allow input and output of POJOs between Tasks, we can use a Bitmap to serialize and
 * deserialize our POJOs so we don't lose the object organization of our class, such as WeatherData
 * (https://stackoverflow.com/questions/51018299/how-to-pass-pojo-class-in-work-manager-in-android)
 */
public class TaskObjectSerializationHelper {
    // serialize WeatherData
    public static String weatherDataSerializeToJSON(WeatherData weatherData) {
        Gson gson = new Gson();
        return gson.toJson(weatherData);
    }

    // deserialize to WeatherData
    public static WeatherData weatherDataDeserializeFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, WeatherData.class);
    }
}
