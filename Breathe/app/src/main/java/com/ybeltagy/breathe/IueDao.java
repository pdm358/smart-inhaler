package com.ybeltagy.breathe;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IueDao {

    @Insert
    void insert(IUE iue);

    @Query("SELECT * from IUE_table")
    // TODO: figure out how to return all IUE's in date_time order
    List<IUE> getallIUEs();
}
