package com.ybeltagy.breathe;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// exportSchema keeps a history of schema versions (for migration)
// TODO: add migration strategy
@Database(entities = {InhalerUsageEvent.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class BreatheRoomDatabase extends RoomDatabase {

    public abstract BreatheDao iueDao();

    private static BreatheRoomDatabase INSTANCE; // this BreatheRoomDatabase is a singleton

    // creates a singleton BreatheRoomDatabase (singleton to prevent multiple instances of the database
    // being opened)
    public static BreatheRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BreatheRoomDatabase.class) {
                if (INSTANCE == null) {
                    // create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BreatheRoomDatabase.class, "IUE_database")
                            // TODO: add migration strategy
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
