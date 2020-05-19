package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "QrReaderDB";
    public static final String EMPLOYEE = "Employee";
    public static final String SHIFT = "Shift";
    public static final String CHECKPOINT = "Checkpoint";
    public static final String LINE = "Line";
    public static final String EVENTS = "Events";

    //employee table
    public static final String KEY_ID = "_id";
    public static final String KEY_WORKER_ID = "WorkerID";
    public static final String KEY_FIRST_NAME_EMPLOYEE = "FirstNameEmployee";
    public static final String KEY_SECOND_NAME_EMPLOYEE = "SecondNameEmployee";
    public static final String KEY_THIRD_NAME_EMPLOYEE = "ThirdNameEmployee";
    public static final String KEY_PHOTO_EMPLOYEE = "PhotoEmployee";

    //shift table
    public static final String KEY_SHIFT_NAME = "ShiftName";
    public static final String KEY_DESCRIPTION = "Description";

    //CheckPointList table
    public static final String KEY_LINE_NUMBER_ID = "LineNumberID";
    public static final String KEY_CHECKPOINT_NAME = "CheckpointName";

    //Lines table
    public static final String KEY_LINE_NAME = "LineName";
    public static final String KEY_SUBMASHINE = "SubMashine";
    public static final String KEY_COMMENTS = "Comments";

    //Events table
    public static final String KEY_DATA = "Data";
    public static final String KEY_TIME = "Time";
    public static final String KEY_SHIFT_ID = "ShiftID";
    public static final String KEY_EMPLOYEE_ID = "EmployeeID";
    public static final String KEY_CHECKPOINT_ID = "CheckpointID";
    public static final String KEY_CHECKED = "Checked";
    public static final String KEY_EVENT_COMMENT = "EventComment";
    public static final String KEY_EVENT_PHOTO = "EventPhoto";


    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables into database
        db.execSQL("create table " + EMPLOYEE + "(" + KEY_ID
                + " integer primary key," + KEY_WORKER_ID + " text," + KEY_FIRST_NAME_EMPLOYEE
                + " text," + KEY_SECOND_NAME_EMPLOYEE + " text,"+ KEY_THIRD_NAME_EMPLOYEE + " text,"
                + KEY_PHOTO_EMPLOYEE + " text" + ")");

        db.execSQL("create table " + SHIFT + "(" + KEY_ID
                + " integer primary key," +  KEY_SHIFT_NAME + " text," + KEY_DESCRIPTION + " text" + ")");

        db.execSQL("create table " + CHECKPOINT + "(" + KEY_ID
                + " integer primary key," + KEY_LINE_NUMBER_ID + " text," + KEY_CHECKPOINT_NAME + " text," + KEY_DESCRIPTION + " text" + ")");

        db.execSQL("create table " + LINE + "(" + KEY_ID
                + " integer primary key," + KEY_LINE_NAME + " text," + KEY_SUBMASHINE + " text," + KEY_COMMENTS + " text" + ")");

        db.execSQL("create table " + EVENTS + "(" + KEY_ID
                + " integer primary key," + KEY_DATA + " text," + KEY_TIME + " text," + KEY_SHIFT_ID + " text,"
                + KEY_EMPLOYEE_ID + " text," + KEY_CHECKPOINT_ID + " text," + KEY_CHECKED + " text," + KEY_EVENT_COMMENT + " text," +
                KEY_EVENT_PHOTO + " text" +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + EMPLOYEE);

        onCreate(db);
    }
}
