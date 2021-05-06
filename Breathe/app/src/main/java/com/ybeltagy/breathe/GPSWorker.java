package com.ybeltagy.breathe;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GPSWorker extends Worker {

    // debug
    String GPS_WORKER_LOG_TAG = "GPSWorker";

    // Result key for GPS
    public static final String KEY_GPS_RESULT = "GPSResult";

    // Location result
    Location currentLocation = null;

    public GPSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // FIXME: this works unreliably; investigate and fix
            Log.d(GPS_WORKER_LOG_TAG, "doWork: start...");

            FusedLocationProviderClient fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(getApplicationContext());
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                }
            };

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setNumUpdates(1);

            try {
                fusedLocationProviderClient
                        .getLastLocation()
                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    currentLocation = task.getResult();
                                    Log.d(GPS_WORKER_LOG_TAG, "Location : " + currentLocation.getLatitude() + " , " + currentLocation.getLongitude());
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                } else {
                    // FIXME:This happens if there is no last location stored (location hasn't been
                    // used in a while) ... I'm not sure how to fix this?
                    // https://developer.android.com/training/location/retrieve-current
                    // actually, if we do what is shown in the link
                    // separately (not in this work manager), it will probably fix it
                    // https://developer.android.com/training/location/request-updates
                                    Log.d(GPS_WORKER_LOG_TAG, "getLastLocation failed");
                                }
                            }
                        });
            } catch (SecurityException e) {
                Log.e(GPS_WORKER_LOG_TAG, e.getMessage());
            }

            try {
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, null);
            } catch (SecurityException e) {
                Log.e(GPS_WORKER_LOG_TAG, e.getMessage());
            }

        return currentLocation != null ? Result.success() : Result.failure();
    }
}
