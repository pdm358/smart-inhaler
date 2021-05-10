package com.ybeltagy.breathe;

import android.content.Context;
import android.location.Location;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;


public class GPSWorker extends ListenableWorker {

    // debug
    String GPS_WORKER_LOG_TAG = "GPSWorker";

    // Result key for GPS
    public static final String KEY_GPS_RESULT = "GPSResult";

    private ListenableFuture<Result> futureResult;
    private LocationCallback mLocationCallback;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public GPSWorker(@NonNull @NotNull Context appContext, @NonNull @NotNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        Log.d(GPS_WORKER_LOG_TAG, "Starting work " + getId());
        return CallbackToFutureAdapter.getFuture( completer -> {

            return mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    Log.d(GPS_WORKER_LOG_TAG, "Work " + getId() + " returned: " + location);

                    // Always set the result as the last operation
                    completer.set(Result.success(createGPSOutput(location)));
                }
            };

        });

    }

    private Data createGPSOutput(Location location) {
        double latLong[] = {location.getLatitude(), location.getLongitude()};
        return new Data.Builder()
                .putDoubleArray(KEY_GPS_RESULT, latLong)
                .build();
    }


}

