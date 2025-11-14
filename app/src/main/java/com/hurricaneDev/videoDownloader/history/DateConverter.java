/*
 * Copyright (c) 2023.  Hurricane Development Studios
 */

package com.hurricaneDev.videoDownloader.history;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.room.TypeConverter;

public class DateConverter {
    private static final String dateFormat = "yyyy MM dd HH mm ss SSS";

    @TypeConverter
    public static Date fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            DateFormat format = new SimpleDateFormat(dateFormat);
            return format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @TypeConverter
    public static String fromDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }
}

