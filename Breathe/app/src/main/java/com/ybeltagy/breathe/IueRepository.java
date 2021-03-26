package com.ybeltagy.breathe;

import android.app.Application;
import android.os.AsyncTask;

/**
 * The IueRepository class:
 * - Handles data operations -> provides clean API for app data
 * - Abstracts access to multiple data sources (such as getting data from a network or
 * cached data from local database)
 * - Manages query threads and allows the use of multiple backends (for future teams)
 */
public class IueRepository {
    private final IueDao iueDao;

    IueRepository(Application app) {
        IueRoomDatabase iueDB = IueRoomDatabase.getDatabase(app); // get handle to database
        iueDao = iueDB.iueDao();
    }

    // wrapper for IueDao insert method
    // - we must use AsyncTask on a non-UI thread (or the app will crash)
    public void insert(IUE iue) {
        new insertAsyncTask(iueDao).execute(iue);
    }

    private static class insertAsyncTask extends AsyncTask<IUE, Void, Void> {
        private final IueDao iueAsyncTaskDao;

        // TODO: find out the not-deprecated way of doing things here
        insertAsyncTask(IueDao dao) {
            iueAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final IUE... params) {
            iueAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
