package com.google.android.gms.samples.vision.barcodereader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapterEmployee {
    private static final String TAG = "DatabaseAdapterEmployee";
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapterEmployee(Context context){
        dbHelper = new DataBaseHelper(context);
        Log.d(TAG, "create helper");
    }

    public DatabaseAdapterEmployee open(){
        database = dbHelper.getReadableDatabase();
        Log.d(TAG, "open db");
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllUsers(){
        String[] columns = new String[] {DataBaseHelper.KEY_WORKER_ID, DataBaseHelper.KEY_FIRST_NAME_EMPLOYEE,
                DataBaseHelper.KEY_SECOND_NAME_EMPLOYEE, DataBaseHelper.KEY_THIRD_NAME_EMPLOYEE};
        return  database.query(DataBaseHelper.EMPLOYEE, columns, null, null, null, null, null);
    }

    public int getIdUser(String userName){
        String[] userNames = userName.split(" ");
        String[] columns = new String[] {DataBaseHelper.KEY_WORKER_ID};
        Cursor cursor = database.query(DataBaseHelper.EMPLOYEE, columns, DataBaseHelper.KEY_FIRST_NAME_EMPLOYEE + " = ? AND " + DataBaseHelper.KEY_SECOND_NAME_EMPLOYEE + " = ? AND " + DataBaseHelper.KEY_THIRD_NAME_EMPLOYEE + " = ?", new String[] {userNames[0],userNames[1],userNames[2]}, null, null, null);;
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_WORKER_ID));
        return Integer.getInteger(id);
    }

    public List<String> getUsers(){
        ArrayList<String> users = new ArrayList<>();
        Cursor cursor = getAllUsers();
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_WORKER_ID));
                String name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_FIRST_NAME_EMPLOYEE));
                String surname = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SECOND_NAME_EMPLOYEE));
                String otherName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_THIRD_NAME_EMPLOYEE));
                users.add(id + ": " + name + " " + surname + " " + otherName);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return  users;
    }

    private Cursor getAllLines(){
        String[] columns = new String[] {DataBaseHelper.KEY_LINE_NAME};
        return  database.query(DataBaseHelper.LINE, columns, null, null, null, null, null);
    }

    public List<String> getLines(){
        ArrayList<String> lines = new ArrayList<>();
        Cursor cursor = getAllLines();
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_LINE_NAME));
                if (!lines.contains(name)){lines.add(name);}
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return  lines;
    }

    public List<String> getSubmachine(String line){
        ArrayList<String> submachines = new ArrayList<>();
        Cursor cursor = getAllSubmachines(line);
        if(cursor.moveToFirst()){
            do{
                String submachine = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SUBMASHINE));
                submachines.add(submachine);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return  submachines;
    }

    private Cursor getAllSubmachines(String line) {
        String selection = DataBaseHelper.KEY_LINE_NAME + " = ?";
        String[] selectionArgs = new String[] { line };
        return  database.query(DataBaseHelper.LINE, null, selection, selectionArgs, null, null, null);
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DataBaseHelper.EMPLOYEE);
    }

}
