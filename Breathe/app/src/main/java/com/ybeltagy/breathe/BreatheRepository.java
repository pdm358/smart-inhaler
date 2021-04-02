package com.ybeltagy.breathe;

import android.app.Application;
import android.os.AsyncTask;

/**
 * The BreatheRepository class:
 * - Handles data operations -> provides clean API for app data
 * - Abstracts access to multiple data sources (such as getting data from a network or
 * cached data from local database)
 * - Manages query threads and allows the use of multiple backends (for future teams)
 */
public class BreatheRepository {
    private final BreatheDao breatheDao;

    BreatheRepository(Application app) {
        BreatheRoomDatabase iueDB = BreatheRoomDatabase.getDatabase(app); // get handle to database
        breatheDao = iueDB.iueDao();
    }

    // wrapper for BreatheDao insert method
    // - we must use AsyncTask on a non-UI thread (or the app will crash)
    public void insert(InhalerUsageEvent inhalerUsageEvent) {
        new insertAsyncTask(breatheDao).execute(inhalerUsageEvent);
    }

    private static class insertAsyncTask extends AsyncTask<InhalerUsageEvent, Void, Void> {
        private final BreatheDao iueAsyncTaskDao;

        // TODO: find out the not-deprecated way of doing things here
        insertAsyncTask(BreatheDao dao) {
            iueAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final InhalerUsageEvent... params) {
            iueAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
