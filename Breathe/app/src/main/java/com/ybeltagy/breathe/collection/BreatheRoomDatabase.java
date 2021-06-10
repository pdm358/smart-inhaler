package com.ybeltagy.breathe.collection;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ybeltagy.breathe.data.BreatheDao;
import com.ybeltagy.breathe.data.InhalerUsageEvent;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.data.Converters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The single database of our Breathe application
 * - contains the Inhaler_Usage_Event_table with InhalerUsageEvent entities
 * - contains the Wearable_Data_table with WearableData entities
 */
@Database(entities = {InhalerUsageEvent.class, WearableData.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class BreatheRoomDatabase extends RoomDatabase {

    public abstract BreatheDao breatheDao();

    private static volatile BreatheRoomDatabase INSTANCE; // this BreatheRoomDatabase is a singleton

    // Executor Service is used (replaced AsyncTask) so inserts may happen concurrently
    protected static final ExecutorService dbWriteExecutor = Executors.newCachedThreadPool();

    // creates a singleton BreatheRoomDatabase
    // (singleton to prevent multiple instances of the database being opened)
    public static BreatheRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BreatheRoomDatabase.class) {
                if (INSTANCE == null) {
                    // create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BreatheRoomDatabase.class, "Breathe_database")
                            .fallbackToDestructiveMigration() // allow for destructive Migration.
                            // Note: leaving implementation of migration strategy to future teams
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
