package com.ybeltagy.breathe.collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ybeltagy.breathe.ble.BLEService;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.collection.BreatheRepository;
import com.ybeltagy.breathe.weather_data_collection.TaskDataFinals;

import java.time.Instant;

public class WearableWorker extends Worker {

    private static final String tag = WearableWorker.class.getName();

    // fixme: what happens if the app is not running when the worker is finished.
    // fixme: are there lifecycle issues?

    Context curContext;

    public WearableWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        this.curContext = context;
    }

    //fixme: does not retry on failure
    @Override
    @SuppressLint("NewApi")
    public Result doWork() {

        Log.d(tag, "started doWork");

        WearableData wearableData = BLEService.getWearableData();

        if(wearableData == null) return Result.failure();

        Instant timestamp = Instant.parse(getInputData().getString(TaskDataFinals.KEY_TIMESTAMP));

        //todo: consider adding a wrapper inside the dao.
        //called synchronously.
        BreatheRoomDatabase.getDatabase(curContext).breatheDao().
                updateWearableData(timestamp, timestamp, wearableData.getTemperature(),
                        wearableData.getHumidity(), wearableData.getPm_count(), wearableData.getCharacter(),
                        wearableData.getDigit());

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
