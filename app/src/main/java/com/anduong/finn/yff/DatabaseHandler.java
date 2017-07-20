package com.anduong.finn.yff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by An Duong on 7/19/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    // Database Name
    //private static String DATABASE_NAME;

    // Contacts table name
    //private static ArrayList<String> TABLE_NAMES;
    private static String[] TABLE_NAMES = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

    // Contacts Table Columns names
    private static final String KEY_EXERCISE = "exercise_name";
    private static final String KEY_LBS = "at_lbs";
    private static final String KEY_REPS = "for_reps";


    public DatabaseHandler(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String TABLE : TABLE_NAMES){
            String CREATE_WEEKDAY_TABLE = "CREATE TABLE " +TABLE + "(" +
                    KEY_EXERCISE    +" TEXT,"+
                    KEY_LBS         +" NUMERIC,"+
                    KEY_REPS        +" NUMERIC"+
                    ")";
            db.execSQL(CREATE_WEEKDAY_TABLE);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        for(String TABLE : TABLE_NAMES){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        }
        // Create tables again
        onCreate(db);
    }

    public void addExercise(String tableName, String exerciseInfo){

        String[] exerciseInfos = exerciseInfo.split(",");// exercise name, lbs, reps
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE, exerciseInfos[0]);
        values.put(KEY_LBS, exerciseInfos[1]);
        values.put(KEY_REPS, exerciseInfos[2]);

        db.insert(tableName, null, values);
        db.close();
    }
    public int getTableCount(String tableName){
        int count = 0;

        String countQuery = "SELECT * FROM " + tableName;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        count = cursor.getCount();
        cursor.close();
        return count;
    }
    public String getAllTableName(){
        String tableNames = "";
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' order by name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tableNames += c.getString( c.getColumnIndex("name")) + "   |   ";
                c.moveToNext();
            }
        }
        return tableNames;
    }
    public ArrayList<String> getRowString(String tableName){
        ArrayList<String> rowStringList = new ArrayList<>();
        String rowString = "";

        String query = "SELECT * FROM " +tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                rowString += cursor.getString(cursor.getColumnIndex(KEY_EXERCISE)) +",";
                rowString += cursor.getString(cursor.getColumnIndex(KEY_LBS)) +",";
                rowString += cursor.getString(cursor.getColumnIndex(KEY_REPS));

                rowStringList.add(rowString);
                cursor.moveToNext();
            }
        }
        return rowStringList;
    }
}
