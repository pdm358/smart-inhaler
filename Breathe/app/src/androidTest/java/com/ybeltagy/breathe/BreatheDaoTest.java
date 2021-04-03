package com.ybeltagy.breathe;

import android.content.Context;

import androidx.room.Room;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

import java.time.OffsetDateTime;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BreatheDaoTest {
    private BreatheDao tBreatheDao;
    private BreatheRoomDatabase tBreatheRoomDatabase;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        tBreatheRoomDatabase = Room.inMemoryDatabaseBuilder(context, BreatheRoomDatabase.class).build();
        tBreatheDao = tBreatheRoomDatabase.breatheDao();
    }

    @After
    public void closeDB() {
        tBreatheDao.deleteAll(); // delete the records in the test database
        tBreatheRoomDatabase.close();
    }

    @Test
    public void updateInhalerUsageEventTest() {
        OffsetDateTime rightNow = OffsetDateTime.now();

        // original InhalerUsageEvent
        InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(rightNow,
                null, null, null);
        tBreatheDao.insert(tInhalerUsageEvent);

        // updated InhalerUsageEvent

        // simulating we gathered the wearableData 3 minutes later
        WearableData wearableData = new WearableData(rightNow.plusMinutes(3));
        int testTemp = 100;
        wearableData.setWearableTemperature(testTemp);
        tInhalerUsageEvent.setWearableData(wearableData);
        tBreatheDao.updateInhalerUsageEvent(tInhalerUsageEvent);

        List<InhalerUsageEvent> allEvents = tBreatheDao.getAllIUEs();
        assertEquals(allEvents.get(0).getWearableData().getWearableTemperature(), testTemp);

        tBreatheDao.deleteAll();
    }

    @Test
    public void eventsBetweenDatesTest() {
        OffsetDateTime now = OffsetDateTime.now();
        // create some inhaler usage events
        for (int i = 0; i < 10; i++) {
            OffsetDateTime aTime = now.minusDays(i);
            InhalerUsageEvent tInhalerUsageEvent = new InhalerUsageEvent(aTime, null,
                    null, new WearableData(now.minusDays(i).plusMinutes(3)));
            tBreatheDao.insert(tInhalerUsageEvent);
        }

        // query between dates
        int week = 7;
        List<InhalerUsageEvent> thisPastWeek =
                tBreatheDao.loadAllInhalerUsageEventsBetweenDates(now.minusDays(week), now);
        assertEquals(thisPastWeek.size(), week + 1);

        tBreatheDao.deleteAll();
    }
}
