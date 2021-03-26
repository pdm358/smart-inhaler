package com.ybeltagy.breathe;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

// exportSchema keeps a history of schema versions (for migration)
// TODO: add migration strategy
@Database(entities = {IUE.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class IueRoomDatabase extends RoomDatabase {

    public abstract IueDao iueDao();

    private static IueRoomDatabase INSTANCE; // this IueRoomDatabase is a singleton

    // creates a singleton IueRoomDatabase (singleton to prevent multiple instances of the database
    // being opened)
    public static IueRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (IueRoomDatabase.class) {
                if (INSTANCE == null) {
                    // create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            IueRoomDatabase.class, "IUE_database")
                            // TODO: add migration strategy
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
