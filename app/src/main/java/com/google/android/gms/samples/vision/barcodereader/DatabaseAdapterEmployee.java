package com.google.android.gms.samples.vision.barcodereader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapterEmployee {
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapterEmployee(Context context){
        dbHelper = new DataBaseHelper(context);
    }

    public DatabaseAdapterEmployee open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {DataBaseHelper.KEY_EMPLOYEE_ID, DataBaseHelper.KEY_FIRST_NAME_EMPLOYEE,
                DataBaseHelper.KEY_SECOND_NAME_EMPLOYEE, DataBaseHelper.KEY_THIRD_NAME_EMPLOYEE};
        return  database.query(DataBaseHelper.EMPLOYEE, columns, null, null, null, null, null);
    }

    public List<String> getUsers(){
        ArrayList<String> users = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_EMPLOYEE_ID));
                String name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_FIRST_NAME_EMPLOYEE));
                String surname = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SECOND_NAME_EMPLOYEE));
                String otherName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SECOND_NAME_EMPLOYEE));
                users.add(id + ": " + name + " " + surname + " " + otherName);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return  users;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DataBaseHelper.EMPLOYEE);
    }

}
