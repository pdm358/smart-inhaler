package com.ybeltagy.breathe;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.Instant;
import java.util.List;

/**
 * Data access object for the BreatheRoomDatabase; allows Android to auto-generate queries without
 * having to use SQL
 */
@Dao
public interface BreatheDao {

    // Insert InhalerUsageEvent into InhalerUsageEvent_table
    @Insert
    void insert(InhalerUsageEvent inhalerUsageEvent);

    /**
     * IMPORTANT: All the timestamp string representations must be of the same size for this
     * method to work correctly.
     *
     * @return all InhalerUsageEvents from the InhalerUsageEvent_table in lexographical,
     * descending order of string timestamp (because of UTC format, this is in chronological order)
     * wrapped as MutableLiveData.
     * MutableLiveData is used so the UI may update if any changes occur to the data and the data
     * may also be changed ("mutable")
     */
    @Query("SELECT * FROM InhalerUsageEvent_table " +
            "ORDER BY Inhaler_Usage_Event_UTC_ISO_8601_date_time DESC")
    LiveData<List<InhalerUsageEvent>> getAllIUEs();

    // Update one or more InhalerUsageEvents
    @Update
    void updateInhalerUsageEvent(InhalerUsageEvent... inhalerUsageEvents);

    /**
     * IMPORTANT: All the timestamp string representations must be of the same size for this
     * method to work correctly.
     *
     * @param firstDate  - Date in range that occurred first
     * @param secondDate - Date in range that occurred last
     * @return InhalerUsageEvents between the parameter dates (inclusive) wrapped as MutableLiveData
     */
    @Query("SELECT * FROM InhalerUsageEvent_table " +
            "WHERE Inhaler_Usage_Event_UTC_ISO_8601_date_time " +
            "BETWEEN :firstDate AND :secondDate")
    LiveData<List<InhalerUsageEvent>> loadAllInhalerUsageEventsBetweenDates(Instant firstDate,
                                                                                   Instant secondDate);

    // Methods for testing ONLY --------------------------------------------------------------------

    // Some duplicated methods used for unit testing the DAO / RoomDatabase without using LiveData
    // wrapper
    @Query("SELECT * FROM InhalerUsageEvent_table " +
            "ORDER BY Inhaler_Usage_Event_UTC_ISO_8601_date_time DESC")
    List<InhalerUsageEvent> getAllIUEsTest();

    @Query("SELECT * FROM InhalerUsageEvent_table " +
            "WHERE Inhaler_Usage_Event_UTC_ISO_8601_date_time " +
            "BETWEEN :firstDate AND :secondDate")
    List<InhalerUsageEvent> loadAllInhalerUsageEventsBetweenDatesTest (Instant firstDate,
                                                                            Instant secondDate);

    // IMPORTANT: this is here for testing use only; it should not be used in the app itself
    // This deletes all InhalerUsageEvents from the InhalerUsageEvent_table
    @Query("DELETE FROM InhalerUsageEvent_table")
    void deleteAll();
}
