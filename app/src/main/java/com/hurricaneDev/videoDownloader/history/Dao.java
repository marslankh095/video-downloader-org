/*
 * Copyright (c) 2023.  Hurricane Development Studios
 */

package com.hurricaneDev.videoDownloader.history;


import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Adding annotation
// to our Dao class
@androidx.room.Dao
public interface Dao {
    @Insert
    void insert(VisitedPage user);

    @Update
    void update(VisitedPage user);

    @Delete
    void delete(VisitedPage user);

    @Query("DELETE FROM history_table WHERE link = :link")
    void deleteBylink(String link);


    @Query("SELECT * FROM history_table ORDER BY myDate ASC")
    List<VisitedPage> getAllHisory();

    @Query("DELETE FROM history_table")
    void deleteAll();
}
//public interface Dao {
//
//    // below method is use to
//    // add data to database.
//    @Insert
//    void insert(VisitedPage model);
//
//    // below method is use to update
//    // the data in our database.
//    @Update
//    void update(VisitedPage model);
//
//    // below line is use to delete a
//    // specific course in our database.
//    @Delete
//    void delete(VisitedPage model);
//
//    // on below line we are making query to
//    // delete all courses from our database.
//    @Query("DELETE FROM history_table")
//    void deleteAllCourses();
//
//    // below line is to read all the courses from our database.
//    // in this we are ordering our courses in ascending order
//    // with our course name.
//    @Query("SELECT * FROM course_table ORDER BY courseName ASC")
//    LiveData<List<VisitedPage>> getAllCourses();
//}

