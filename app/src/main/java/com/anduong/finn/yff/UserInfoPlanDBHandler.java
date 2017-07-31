package com.anduong.finn.yff;

import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.getCurrentDate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by NEW PC on 7/23/2017.
 */

public class UserInfoPlanDBHandler extends UserInfoDBHandler{
    private static String DATABASE_NAME = "userDB";

    //Plan Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_EXERCISE_MAP = "exercise_map";
    private static final String KEY_CURRENT_WEIGHT = "current_weight";
    private static final String KEY_GOAL_WEIGHT = "goal_weight";
    private static final String KEY_COMMENT = "comment";
    private static final String[] KEY_LIST = {KEY_ID ,KEY_DATE, KEY_EXERCISE_MAP,KEY_CURRENT_WEIGHT, KEY_GOAL_WEIGHT,KEY_COMMENT};

    public UserInfoPlanDBHandler(Context context) {
        super(context);
    }

    public String startNewPlan(String planName, double goalWeight, String map){
        String newPlan = planName;
        int count = 0;

        for(String tableName : getAllTableName()){
            String[] temp = tableName.split("_");
            if(temp.length > 1){
                {
                    String temp1 = temp[0]+"_"+temp[1];
                    if(temp1.equals(planName)){
                        count++;
                    }
                }
            }
        }
        for(String tableName : getAllTableName()){
            String[] temp = tableName.split("_");
            if(temp.length > 1){// if not user
                {
                    String temp1 = temp[0]+"_"+temp[1];
                    debugLog(temp1 + " temp1");
                    int count1 = 0 ;
                    if(temp1.equals(planName)){
                        if(temp.length > 2){
                            debugLog("Triggered : "+(count1++));
                            temp[2] = Integer.toString(count);
                            newPlan += "_" + temp[2];
                        }else {
                            newPlan += "_" + count;
                        }
                    }
                }
            }

        }
        createTableIfNotExist(newPlan);
        insertNewRowInto(newPlan,goalWeight, map);

        return newPlan;
    }
    public void insertNewRowInto(String planName,double goalWeight ,String map){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE,getCurrentDate());
        values.put(KEY_EXERCISE_MAP, map);
        values.put(KEY_CURRENT_WEIGHT, 0);
        values.put(KEY_GOAL_WEIGHT, goalWeight);
        values.put(KEY_COMMENT, "none");

        db.insert(planName, null, values);
        db.close();
    }
    public ArrayList<String> getAllTableName(){
        ArrayList<String> tableNames = new ArrayList<>();
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' order by name";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tableNames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        c.close();
        return tableNames;
    }
    public int getTableCount(){
        int count = 0;
        String countQuery = "SELECT name FROM sqlite_master WHERE type='table'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(countQuery, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                count++;
                c.moveToNext();
            }
        }

        c.close();
        db.close();
        return count;
    }

    public void updateExerciseMapAt(String TABLE_NAME, int ROW_ID, String map){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_EXERCISE_MAP + "='" + map+"' WHERE id = " + ROW_ID;
        db.execSQL(query);
    }
    public void updateCurrentWeightAt(String TABLE_NAME, int ROW_ID, String currentWeight){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_WEIGHT + "='" + currentWeight+"' WHERE id = " + ROW_ID;
        db.execSQL(query);
    }
    public void updateCommentAt(String TABLE_NAME, int ROW_ID, String comment){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_COMMENT + "='" + comment+"' WHERE id = " + ROW_ID;

        db.execSQL(query);
    }


    public ArrayList<String> getRowString(String tableName){
        ArrayList<String> rowStringList = new ArrayList<>();
        String rowString = "";

        String query = "SELECT * FROM " +tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){

                for(String KEY : KEY_LIST){
                    rowString += cursor.getString(cursor.getColumnIndex(KEY)) +",";
                }
                rowStringList.add(rowString);

                rowString = "";
                cursor.moveToNext();
            }
        }
        return rowStringList;
    }
    private void createTableIfNotExist(String tableName){
        String query = "CREATE TABLE IF NOT EXISTS " + tableName+     "(" +
                KEY_ID                      +" INTEGER PRIMARY KEY,"+
                KEY_DATE                    +" TEXT,"+
                KEY_EXERCISE_MAP            +" TEXT,"+ //1 = complete, 0 = missed, ? = not yet reached
                KEY_CURRENT_WEIGHT          +" NUMERIC,"+
                KEY_GOAL_WEIGHT             +" NUMERIC,"+
                KEY_COMMENT                 +" TEXT"+
                ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }
    public void deleteTable(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
