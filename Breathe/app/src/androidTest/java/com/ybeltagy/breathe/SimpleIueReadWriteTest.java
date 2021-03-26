package com.ybeltagy.breathe;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SimpleIueReadWriteTest {
    private IueDao tIueDao;
    private IueRoomDatabase tIueRoomDatabase;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        tIueRoomDatabase = Room.inMemoryDatabaseBuilder(context, IueRoomDatabase.class).build();
        tIueDao = tIueRoomDatabase.iueDao();
    }

    @After
    public void closeDB() throws IOException {
        tIueDao.deleteAll(); // delete the records in the test database
        tIueRoomDatabase.close();
    }

    @Test
    public void writeTrivialIueAndReadInList() throws Exception {
        OffsetDateTime rightNow = OffsetDateTime.now();
        IUE tIue = new IUE(rightNow, null, null, null);

        tIueDao.insert(tIue);
        List<IUE> byTimeStamp = tIueDao.getallIUEs();

        assert(byTimeStamp.get(0).getTimeStamp().equals(rightNow));
    }
}
