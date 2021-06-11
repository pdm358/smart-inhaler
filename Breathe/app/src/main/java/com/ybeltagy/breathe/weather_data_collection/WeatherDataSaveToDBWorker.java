package com.ybeltagy.breathe.weather_data_collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ybeltagy.breathe.ble.BLEService;
import com.ybeltagy.breathe.collection.BreatheRoomDatabase;
import com.ybeltagy.breathe.collection.WearableWorker;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.data.WeatherData;

import java.time.Instant;


//fixme: rather than implementing a worker just for saving, it makes more sense to modify
// weatherAPIWorker so it takes a parameter and either saves into the database or returns the jsonString depending on that variable.
public class WeatherDataSaveToDBWorker extends Worker {

    private static final String tag = WeatherDataSaveToDBWorker.class.getName();

    Context context;

    public WeatherDataSaveToDBWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressLint("NewApi")
    public Result doWork() {
        Log.d(tag, "started work");

        // check if output from WeatherAPIWorker is null
        String apiResponse = getInputData().getString(TaskDataFinals.KEY_WEATHER_DATA_RESULT);
        if (apiResponse == null) {
            return Result.failure();
        }

        // check if constructed weather data is valid
        WeatherData weatherData = CollectWeatherData.responseJSONToWeatherData(apiResponse);
        if (weatherData == null || !weatherData.isDataValid()) {
            return Result.failure();
        }

        String timeStampInput = getInputData().getString(TaskDataFinals.KEY_TIMESTAMP);
        // check if input timestamp string is null
        if (timeStampInput == null) {
            Log.d(tag, "Input timestamp string was null");
            return Result.failure();
        }
        Instant timestamp = Instant.parse(timeStampInput);

        BreatheRoomDatabase.getDatabase(context).breatheDao().
                updateWeatherData(timestamp,
                        weatherData.getWeatherTemperature(),
                        weatherData.getWeatherHumidity(),
                        weatherData.getWeatherPrecipitationIntensity(),
                        weatherData.getWeatherTreeIndex(),
                        weatherData.getWeatherGrassIndex(),
                        weatherData.getWeatherEPAIndex());

        // Success! We've saved to the database
        // debug
        Log.d(tag, "Saved to database: WeatherData ->" + timestamp + " { "
                + weatherData.getWeatherTemperature() + " Celsius, "
                + weatherData.getWeatherHumidity() + "% humidity, "
                + weatherData.getWeatherPrecipitationIntensity() + " mm/hr, "
                + weatherData.getWeatherTreeIndex() + " (tree index), "
                + weatherData.getWeatherGrassIndex() + " (grass index), " +
                weatherData.getWeatherEPAIndex() + "(AQI)");
        return Result.success();

    }
}
