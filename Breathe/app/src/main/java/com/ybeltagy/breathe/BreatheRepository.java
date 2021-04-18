package com.ybeltagy.breathe;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;

/**
 * The BreatheRepository class:
 * - Handles data operations -> provides clean API for app data
 * - Abstracts access to multiple data sources (such as getting data from a network or
 * cached data from local database)
 * - Manages query threads and allows the use of multiple backends (for future teams)
 */
public class BreatheRepository {
    private final BreatheDao breatheDao;
    private final LiveData<List<InhalerUsageEvent>> allInhalerUsageEvents; // TODO: IDE wants to make this final, does this cause problems?


    BreatheRepository(Application app) {
        BreatheRoomDatabase breatheDB = BreatheRoomDatabase.getDatabase(app); // get handle to database
        breatheDao = breatheDB.breatheDao();
        allInhalerUsageEvents = breatheDao.getAllIUEs();
    }

    LiveData<List<InhalerUsageEvent>> getAllInhalerUsageEvents() {
        return allInhalerUsageEvents;
    }

    /**
     * Wrapper for BreatheDao insert method
     * - inserts a single InhalerUsageEvent
     *
     * @param inhalerUsageEvent
     */
    public void insert(final InhalerUsageEvent inhalerUsageEvent) {
        BreatheRoomDatabase.dbWriteExecutor.execute(() -> breatheDao.insert(inhalerUsageEvent));
    }

    /**
     * TODO: maybe we should never use this because it "clobbers" our existing IUEs (unless we
     * can also retrieve the existing inhalerUsageEvent, update the data and use the same
     * inhalerUsageEvent object as the input to this function) (might want to delete it)
     * <p>
     * wrapper for BreatheDao update method
     * - uses Executor Service (non-UI thread)
     *
     * @param inhalerUsageEvent
     */
    public void update(InhalerUsageEvent inhalerUsageEvent) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
                -> breatheDao.updateInhalerUsageEvent(inhalerUsageEvent));
    }

    /**
     * Note: Use this one to update an existing inhalerUsageEvent with DiaryEntry data so the
     * existing other inner objects (WearableData, WeatherData) don't get "clobbered"
     *
     * @param timeStamp
     * @param tag
     * @param diaryMessage
     */
    public void updateDiaryEntry(Instant timeStamp, Tag tag, String diaryMessage) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
                -> breatheDao.updateDiaryEntry(timeStamp, tag, diaryMessage));
    }

    /**
     * Note: Use this one to update an existing inhalerUsageEvent with WearableData data so the
     * existing other inner objects (DiaryEntry, WeatherData) don't get "clobbered"
     *
     * @param inhalerUsageTimeStamp
     * @param wearableDataTimeStamp
     * @param temperature
     * @param humidity
     * @param character
     * @param digit
     */
    public void updateWearableData(Instant inhalerUsageTimeStamp, Instant wearableDataTimeStamp, float temperature, float humidity, char character, char digit) {
        BreatheRoomDatabase.dbWriteExecutor.execute(() ->
                breatheDao.updateWearableData(
                        inhalerUsageTimeStamp, wearableDataTimeStamp, temperature, humidity,
                        character, digit));
    }

    // Methods for TESTING PURPOSES ONLY------------------------------------------------------------

    /**
     * IMPORTANT : This is for testing purposes ONLY -> to clear away placeholder IUEs we've created
     * as we develop and test the app.
     */
    public void deleteAllInhalerUsageEvents() {
        BreatheRoomDatabase.dbWriteExecutor.execute(breatheDao::deleteAll);
    }


}
