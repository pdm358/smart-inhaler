package com.ybeltagy.breathe;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.OffsetDateTime;
import java.util.List;

@Dao
public interface BreatheDao {

    // Insert InhalerUsageEvent into InhalerUsageEvent_table
    @Insert
    void insert(InhalerUsageEvent inhalerUsageEvent);

    // IMPORTANT: this is here for testing use only; it should not be used in the app itself
    // This deletes all InhalerUsageEvents from the InhalerUsageEvent_table
    @Query("DELETE FROM InhalerUsageEvent_table")
    void deleteAll();

    // Returns all InhalerUsageEvents from the InhalerUsageEvent_table in lexographical ascending order
    // of string timestamp (because of UTC format, this is in chronological order)
    @Query("SELECT * FROM InhalerUsageEvent_table ORDER BY UTC_ISO_8601_date_time ASC")
    List<InhalerUsageEvent> getallIUEs();

    // Update one or more InhalerUsageEvents
    // @return : number of entries updated
    @Update
    int updateInhalerUsageEvent(InhalerUsageEvent... inhalerUsageEvents);


    // Return InhalerUsageEvents between the parameter dates
    @Query("SELECT * FROM InhalerUsageEvent_table WHERE UTC_ISO_8601_date_time " +
            "BETWEEN :firstDate AND :secondDate")
    List<InhalerUsageEvent> loadAllInhalerUsageEventsBetweenDates(OffsetDateTime firstDate,
                                                                         OffsetDateTime secondDate);
}
