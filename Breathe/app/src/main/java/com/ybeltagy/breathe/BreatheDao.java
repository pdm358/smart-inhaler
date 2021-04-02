package com.ybeltagy.breathe;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BreatheDao {

    // Insert InhalerUsageEvent into IUE_table
    @Insert
    void insert(InhalerUsageEvent inhalerUsageEvent);

    // IMPORTANT: this is here for testing use only; it should not be used in the app itself
    // This deletes all IUEs from the IUE_table
    @Query("DELETE FROM InhalerUsageEvent_table")
    void deleteAll();

    // Returns all IUEs from the IUE_table in lexographical ascending order of string timestamp
    // (because of UTC format, this is in chronological order)
    @Query("SELECT * FROM InhalerUsageEvent_table ORDER BY UTC_ISO_8601_date_time ASC")
    List<InhalerUsageEvent> getallIUEs();

    // TODO: query for between dates
}
