package com.ybeltagy.breathe.collection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ybeltagy.breathe.ble.BLEService;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.collection.BreatheRepository;

import java.time.Instant;

public class WearableWorker extends Worker {

    Instant timestamp = null;
    // fixme: is this the best way to pass the repository?
    // fixme: what happens if the app is not running when the worker is finished.
    // fixme: are there lifecycle issues?
    BreatheRepository breatheRepository = null;

    public WearableWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params,
            @NonNull BreatheRepository breatheRepository,
            @NonNull Instant timestamp) {
        super(context, params);

        this.breatheRepository = breatheRepository;
        this.timestamp = timestamp;
    }

    @Override
    public Result doWork() {

        WearableData wearableData = BLEService.getWearableData();

        if(wearableData == null) return Result.failure();

        breatheRepository.updateWearableData(timestamp, wearableData); // Todo: consider making a synchronous database save.

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
