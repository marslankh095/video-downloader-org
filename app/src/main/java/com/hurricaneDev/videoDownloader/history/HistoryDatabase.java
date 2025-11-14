/*
 * Copyright (c) 2023.  Hurricane Development Studios
 */

package com.hurricaneDev.videoDownloader.history;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// adding annotation for our database entities and db version.
@Database(entities = {VisitedPage.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract Dao taskDao();
}