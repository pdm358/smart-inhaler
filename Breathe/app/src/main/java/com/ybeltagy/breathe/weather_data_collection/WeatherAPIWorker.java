package com.ybeltagy.breathe.weather_data_collection;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ybeltagy.breathe.R;
import com.ybeltagy.breathe.data.WeatherData;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import static com.ybeltagy.breathe.weather_data_collection.TaskDataFinals.KEY_WEATHER_DATA_RESULT;

/**
 * Worker class that takes a double array of latitude, longitude and requests weather data from
 * tomorrow.io's v4 API
 */
public class WeatherAPIWorker extends Worker {
    // debug
    String WEATHER_API_WORKER_TAG = "WeatherAPIWorker";

    // handle to Context from constructor
    Context context;

    public WeatherAPIWorker(@NonNull @NotNull Context context,
                            @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    /**
     * Get weather data from tomorrow.io
     *
     * @return Result.success() if valid weather data was returned
     * or Result.failure if valid weather data wasn't returned from API call
     */
    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        // TODO: should this be a serialized Location object instead of a double array of latitude
        //       and longitude?
        double[] latLongArray = getInputData().getDoubleArray(TaskDataFinals.KEY_GPS_RESULT);

        if (latLongArray == null) throw new AssertionError();
        Log.d(WEATHER_API_WORKER_TAG,
                "Received location in WeatherAPIWorker: "
                        + latLongArray[0] + " , " + latLongArray[1]);

        CollectWeatherData.apiKey = context.getString(R.string.clima_cell_api_key);
        WeatherData returnWeatherData =
                CollectWeatherData.syncGetWeatherData(Calendar.getInstance(),
                        latLongArray[0], latLongArray[1]);

        if (returnWeatherData != null) {
            // set output
            // - serialize our WeatherData object
            String serializedWeatherData =
                    TaskObjectSerializationHelper.weatherDataSerializeToJSON(returnWeatherData);
            Log.d(WEATHER_API_WORKER_TAG,
                    "Serialized weather data -> "
                            + serializedWeatherData);
            Data weatherDataOutput = new Data.Builder()
                    .putString(KEY_WEATHER_DATA_RESULT, serializedWeatherData)
                    .build();
            return Result.success(weatherDataOutput);

        } else { // we didn't get any weather data
            Log.d(WEATHER_API_WORKER_TAG,
                    "Weather data was null");
            return Result.failure();
        }
    }
}
