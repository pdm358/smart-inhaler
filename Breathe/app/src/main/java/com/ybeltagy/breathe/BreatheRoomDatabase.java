package com.ybeltagy.breathe;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {InhalerUsageEvent.class, WearableData.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class BreatheRoomDatabase extends RoomDatabase {

    public abstract BreatheDao breatheDao();

    private static volatile BreatheRoomDatabase INSTANCE; // this BreatheRoomDatabase is a singleton
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // creates a singleton BreatheRoomDatabase (singleton to prevent multiple instances of the database
    // being opened)
    public static BreatheRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BreatheRoomDatabase.class) {
                if (INSTANCE == null) {
                    // create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BreatheRoomDatabase.class, "Breathe_database")
                            // Note: leaving implementation of migration strategy to future teams
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
