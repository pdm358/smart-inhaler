package com.ybeltagy.breathe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class WeatherDataWorker extends Worker{

    // debug
    String WEATHER_DATA_WORKER_LOG_TAG = "WeatherDataWorker";

    // Result key for GPS
    public static final String KEY_GPS_RESULT = "GPSResult";

    // Location result
    Location currentLocation = null;

    public WeatherDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // TODO: request permissions if not granted
        // FIXME: Can we ask for permissions in a workmanager? probably not
        if (ActivityCompat.checkSelfPermission(
                this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(WEATHER_DATA_WORKER_LOG_TAG, "Permission was not granted for GPS");
            return Result.failure();
        }

        // get GPS location for the ClimaCell weather API call

        FusedLocationProviderClient fusedLocationProviderClient
                = LocationServices.getFusedLocationProviderClient(this.getApplicationContext());

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setNumUpdates(1);

        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    currentLocation = task.getResult();
                    Log.d(WEATHER_DATA_WORKER_LOG_TAG, "Got location : " + currentLocation.getLatitude() + " , " + currentLocation.getLongitude());
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }
                else {
                    // FIXME:This happens if there is no last location stored (location hasn't been
                    // used in a while) ... I'm not sure how to fix this?
                    // https://developer.android.com/training/location/retrieve-current
                    // actually, if we do what is shown in the link
                    // separately (not in this work manager), it will probably fix it
                    // https://developer.android.com/training/location/request-updates
                    Log.d(WEATHER_DATA_WORKER_LOG_TAG, "getLastLocation failed");
                }
            });
        }
        catch (SecurityException e) {
            Log.e(WEATHER_DATA_WORKER_LOG_TAG, e.getMessage());
        }
        // if we couldn't even get the GPS location, we've failed
        if (currentLocation == null) {
            Log.d(WEATHER_DATA_WORKER_LOG_TAG, "currentLocation is null");
            return Result.failure();
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, null);
        // next, call ClimaCell API call here

        return Result.success();
    }
}
