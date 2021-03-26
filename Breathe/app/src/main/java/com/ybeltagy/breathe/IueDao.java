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

    // TODO: figure out how to return all IUE's in date_time order
    // Returns all IUEs from the IUE_table
    @Query("SELECT * from IUE_table")
    List<IUE> getallIUEs();
}
