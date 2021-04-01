package com.ybeltagy.breathe;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IueDao {

    // Insert IUE into IUE_table
    @Insert
    void insert(IUE iue);

    // IMPORTANT: this is here for testing use only; it should not be used in the app itself
    // This deletes all IUEs from the IUE_table
    @Query("DELETE FROM IUE_table")
    void deleteAll();

    // Returns all IUEs from the IUE_table in lexographical ascending order of string timestamp
    // (because of UTC format, this is in chronological order)
    @Query("SELECT * FROM IUE_table ORDER BY UTC_ISO_8601_date_time ASC")
    List<IUE> getallIUEs();

    // TODO: query for between dates
}
