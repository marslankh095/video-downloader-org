/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.hurricaneDev.videoDownloader.history;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "history_table")
public class VisitedPage {
    @PrimaryKey(autoGenerate = true)
    int id ;
    public String title;
    public String link;
    @TypeConverters(DateConverter.class)
    public Date myDate;
}
