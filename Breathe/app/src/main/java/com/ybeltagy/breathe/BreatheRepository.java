package com.ybeltagy.breathe;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
     * wrapper for BreatheDao update method
     * - uses Executor Service (non-UI thread)
     *
     * @param inhalerUsageEvent
     */
    public void update(InhalerUsageEvent inhalerUsageEvent) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
                -> breatheDao.updateInhalerUsageEvent(inhalerUsageEvent));
    }

    public void updateDiaryEntry(Instant timeStamp, Tag tag, String diaryMessage) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
                -> breatheDao.updateDiaryEntry(timeStamp, tag, diaryMessage));
    }

    public void updateWearableData(Instant timestamp, float temperature, float humidity, char character, char digit) {
        BreatheRoomDatabase.dbWriteExecutor.execute(()
        -> breatheDao.updateWearableData(timestamp, temperature, humidity, character, digit));
    }

    /**
     * Wrapper for BreatheDao getInhalerUsageEventWithTimeStamp() method
     * TODO: does using the Dao directly without an executor cause issues? Should this be LiveData?
     * - gets the single InhalerUsageEvent that has the input parameter Instant timestamp primary key
     * @param timeStamp
     * @return
     */
    public List<InhalerUsageEvent> getInhalerUsageEventWithTimeStamp(Instant timeStamp) {
        return breatheDao.getInhalerUsageEventWithTimeStamp(timeStamp);
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
