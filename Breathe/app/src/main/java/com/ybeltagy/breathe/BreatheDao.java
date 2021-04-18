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


    // TODO: maybe we should never use this because it overwites/"clobbers" our existing IUEs
    //       (unless we can also retrieve the existing inhalerUsageEvent, update the data and use
    //       the same inhalerUsageEvent object as the input to this function)

    /**
     * Updates an existing inhalerUsageEvent(s) with the input inhalerUsageEvent(s)
     *
     * @param inhalerUsageEvents one or more inhaler usage events
     */
    @Update
    void updateInhalerUsageEvent(InhalerUsageEvent... inhalerUsageEvents);

    /**
     * Gets a single InhalerUsageEvent from the database.
     * - This is used to query the database and check if its empty or not
     * - Note: Does not return a LiveData-wrapped InhalerUsageEvent
     *
     * @return a list containing at most a single InhalerUsageEvent
     */
    @Query("SELECT * FROM InhalerUsageEvent_table LIMIT 1")
    List<InhalerUsageEvent> getAnySingleInhalerUsageEvent();

    /**
     * Note: Use this one to update an existing inhalerUsageEvent with DiaryEntry data so the
     * existing other inner objects (WearableData, WeatherData) don't get "clobbered"
     *
     * @param timeStamp
     * @param tag
     * @param diaryMessage
     * @return the number of records updated (should only be 1)
     */
    @Query("UPDATE InhalerUsageEvent_table SET tag = :tag, message = :diaryMessage " +
            "WHERE Inhaler_Usage_Event_UTC_ISO_8601_date_time = :timeStamp")
    int updateDiaryEntry(Instant timeStamp, Tag tag, String diaryMessage);

    /**
     * Note: Use this one to update an existing inhalerUsageEvent with WearableData data so the
     * existing other inner objects (DiaryEntry, WeatherData) don't get "clobbered"
     *
     * @param inhalerUsageTimeStamp - when the inhalerUsageEvent occurred
     * @param wearableDataTimeStamp - when the wearableData was collected
     * @param temp
     * @param humid
     * @param character
     * @param digit
     * @return the number of records updated (should only be 1)
     */
    @Query("UPDATE InhalerUsageEvent_table " +
            "SET Wearable_Data_UTC_ISO_8601_date_time = :wearableDataTimeStamp, temperature = :temp, " +
            "humidity = :humid, character = :character, " +
            "digit = :digit " +
            "WHERE Inhaler_Usage_Event_UTC_ISO_8601_date_time = :inhalerUsageTimeStamp")
    int updateWearableData(Instant inhalerUsageTimeStamp, Instant wearableDataTimeStamp,
                           float temp, float humid, char character, char digit);

    /**
     * Note: This method alone is fine but there is no wrapper method for this in the Breathe
     * repository.  This is because, currently, I (Sarah) could not find a good way to return
     * an InhalerUsageEvent using an ExectorService
     * <p>
     * Get the single InhalerUsageEvent that has the input parameter Instant timestamp primary key
     *
     * @param timeStamp timeStamp of the InhalerUsageEvent we want
     * @return List of InhalerUsageEvents that have the input timeStamp; there should only be 1 as
     * we are currently using the Instant timeStamp as the primary key
     */
    @Query("SELECT * FROM InhalerUsageEvent_table " +
            "WHERE Inhaler_Usage_Event_UTC_ISO_8601_date_time == :timeStamp")
    List<InhalerUsageEvent> getInhalerUsageEventWithTimeStamp(Instant timeStamp);

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
    List<InhalerUsageEvent> loadAllInhalerUsageEventsBetweenDatesTest(Instant firstDate,
                                                                      Instant secondDate);

    // IMPORTANT: this is here for testing use only; it should not be used in the app itself
    // This deletes all InhalerUsageEvents from the InhalerUsageEvent_table
    @Query("DELETE FROM InhalerUsageEvent_table")
    void deleteAll();
}
