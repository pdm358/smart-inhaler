package com.ybeltagy.breathe.collection;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.ybeltagy.breathe.data.BreatheDao;
import com.ybeltagy.breathe.data.DiaryEntry;
import com.ybeltagy.breathe.data.InhalerUsageEvent;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.data.WeatherData;
import com.ybeltagy.breathe.weather_data_collection.GPSWorker;
import com.ybeltagy.breathe.weather_data_collection.TaskDataFinals;
import com.ybeltagy.breathe.weather_data_collection.WeatherAPIWorker;
import com.ybeltagy.breathe.weather_data_collection.WeatherDataSaveToDBWorker;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The BreatheRepository class:
 * - Handles data operations -> provides clean API for app data
 * - Abstracts access to multiple data sources (such as getting data from a network or
 * cached data from local database)
 * - Manages query threads and allows the use of multiple backends (for future teams)
 */
public class BreatheRepository {
    private final BreatheDao breatheDao;
    private final LiveData<List<InhalerUsageEvent>> allInhalerUsageEvents;


    public BreatheRepository(Application app) {
        BreatheRoomDatabase breatheDB = BreatheRoomDatabase.getDatabase(app); // get handle to database
        breatheDao = breatheDB.breatheDao();
        allInhalerUsageEvents = breatheDao.getAllIUEs();
    }

    public LiveData<List<InhalerUsageEvent>> getAllInhalerUsageEvents() {
        return allInhalerUsageEvents;
    }

    /**
     * Wrapper for BreatheDao insert method
     * - inserts a single InhalerUsageEvent
     *
     * @param inhalerUsageEvent
     */
    public void insertIUE(final InhalerUsageEvent inhalerUsageEvent) {
        BreatheRoomDatabase.dbWriteExecutor.execute(() -> breatheDao.insert(inhalerUsageEvent));
    }

    /**
     * TODO: maybe we should never use this because it "clobbers" our existing IUEs (unless we
     *  can also retrieve the existing inhalerUsageEvent, update the data and use the same
     *  inhalerUsageEvent object as the input to this function) (might want to delete it)
     * <p>
     * wrapper for BreatheDao update method
     * - uses Executor Service (non-UI thread)
     *
     * @param inhalerUsageEvent
     */
    public void updateIUE(InhalerUsageEvent inhalerUsageEvent) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
                -> breatheDao.updateInhalerUsageEvent(inhalerUsageEvent));
    }

    /**
     * Note: Use this one to update an existing inhalerUsageEvent with DiaryEntry data so the
     * existing other inner objects (WearableData, WeatherData) don't get "clobbered"
     *
     * @param timeStamp
     * @param diaryEntry
     */
    public void updateDiaryEntry(Instant timeStamp, DiaryEntry diaryEntry) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
                -> breatheDao.updateDiaryEntry(timeStamp, diaryEntry.getTag(), diaryEntry.getMessage()));
    }

    /**
     * Note: Use this one to update an existing inhalerUsageEvent with WearableData data so the
     * existing other inner objects (DiaryEntry, WeatherData) don't get "clobbered"
     *
     * @param inhalerUsageTimeStamp
     * @param wearableData
     */
    public void updateWearableData(Instant inhalerUsageTimeStamp, WearableData wearableData) {
        BreatheRoomDatabase.dbWriteExecutor.execute(() ->
                breatheDao.updateWearableData(
                        inhalerUsageTimeStamp,
                        wearableData.getWearableDataTimeStamp(),
                        wearableData.getTemperature(),
                        wearableData.getHumidity(),
                        wearableData.getCharacter(),
                        wearableData.getDigit()));
    }

    /**
     * Updates an existing inhalerUsageEvent with WeatherData without overwriting
     * other existing inner objects (DiaryEntry, WeatherData)
     *
     * @param inhalerUsageTimeStamp
     * @param weatherData
     */
    public void updateWeatherData(Instant inhalerUsageTimeStamp, WeatherData weatherData) {
        BreatheRoomDatabase.dbWriteExecutor.execute(() ->
                breatheDao.updateWeatherData(
                        inhalerUsageTimeStamp,
                        weatherData.getWeatherTemperature(),
                        weatherData.getWeatherHumidity(),
                        weatherData.getWeatherPrecipitationIntensity(),
                        weatherData.getWeatherTreeIndex(),
                        weatherData.getWeatherGrassIndex(),
                        weatherData.getWeatherEPAIndex()));
    }


    /**
     * Saves the IUE into the database and uses the workmanager to collect the other data.
     * <P>
     * Unfortunately, the behavior of the workmanager once the app closes is
     * <a href=https://stackoverflow.com/questions/50682061/android-is-workmanager-running-when-app-is-closed>not well defined.</a>
     * @param timestamp the IUE timestamp
     * @param context the calling context
     */
    @SuppressLint("NewApi")
    public void startDataCollection(Instant timestamp, Context context){

        // fixme: this method looks weird with part of its logic implemented direclty in it and another in a helper.
        //  refactor later.
        InhalerUsageEvent iue = new InhalerUsageEvent(timestamp);

        insertIUE(iue);

        //fixme: this workrequest does not retry. There is probably something wrong with the parameters.
        // Something like if I want to retry, I have to specify that in the response.
        WorkRequest wearableWorkRequest =
                new OneTimeWorkRequest
                        .Builder(WearableWorker.class)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .setInputData(
                                new Data.Builder().putString(
                                        TaskDataFinals.KEY_TIMESTAMP, timestamp.toString()).build())
                        // todo: Merge the weather data package and this package. It is not a good idea to refer to a final in another package.
                        .build();

        WorkManager
                .getInstance(context)
                .enqueue(wearableWorkRequest);

        // get WeatherData for this IUE
        getAndSaveWeatherDataHelper(timestamp, context);

    }

    @SuppressLint("NewApi")
    private void getAndSaveWeatherDataHelper(Instant timestamp, Context context) {
        // create work request for GPS
        WorkManager dataFlowManager = WorkManager.getInstance(context);
        OneTimeWorkRequest gpsRequest = new OneTimeWorkRequest
                .Builder(GPSWorker.class)
                .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS).build();

        //  create work request for online weather data for the GPS location
        OneTimeWorkRequest weatherAPIRequest =
                new OneTimeWorkRequest.Builder(WeatherAPIWorker.class)
                        .setInputData( new Data.Builder().putString(
                                TaskDataFinals.KEY_TIMESTAMP, timestamp.toString()).build())
                        .build();

        // create work request to save weatherData object to the database
        OneTimeWorkRequest saveWeatherRequest =
                new OneTimeWorkRequest.Builder(WeatherDataSaveToDBWorker.class)
                .setInputData( new Data.Builder().putString(
                        TaskDataFinals.KEY_TIMESTAMP, timestamp.toString()).build())
                        // note: also gets input from weatherAPIRequest String output
                        // fixme: might as well have weatherAPIRequest pass the timestamp.
                .build();

        dataFlowManager
                .beginWith(gpsRequest)
                .then(weatherAPIRequest)
                .then(saveWeatherRequest)
                .enqueue();
    }

}