package com.anduong.finn.yff;

/**
 * Created by an duong on 7/21/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class UserInfoDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static String DATABASE_NAME = "userDB";

    private static String KEY_ID = "id";

    //Plan Table Columns names
    private static final String KEY_PLAN_NAME = "plan_name";
    private static final String KEY_START_WEEK = "start_week";
    private static final String KEY_END_WEEK = "end_week";
    private static final String KEY_WORKOUT_COMPLETED = "work_out_completed";
    private static final String KEY_NUM_EXERCISE_COMPLETED = "";
    private static final String KEY_NUM_REPS_COMPLETED = "";
    private static final String KEY_MAX = "";
    private static final String KEY_MISSED_WORKOUT = "";
    private static final String KEY_RATING = "";
    private static final String[] KEYS = {KEY_PLAN_NAME, KEY_START_WEEK, KEY_END_WEEK, KEY_WORKOUT_COMPLETED, KEY_NUM_EXERCISE_COMPLETED,KEY_NUM_REPS_COMPLETED,KEY_MAX,KEY_MISSED_WORKOUT,KEY_RATING};

    // User Info
    private static final String TABLE_NAME = "user";
    private static final String KEY_USER = "user_name";
    private static final String KEY_CURRENT_WEIGHT = "current_weight";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_PLAN_START = "plan_started";
    private static String userName;
    private static double userCurrentWeight;

    public UserInfoDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public void setUser(String userName, double userCurrentWeight){
        this.userName = userName;
        this.userCurrentWeight = userCurrentWeight;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        createUserTableIfNotExist();

        values.put(KEY_USER, userName);
        values.put(KEY_CURRENT_WEIGHT, userCurrentWeight);
        values.put(KEY_START_DATE, Utilities.getCurrentDate());
        values.put(KEY_PLAN_START, "no");

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    private void createUserTableIfNotExist(){
        SQLiteDatabase db = this.getWritableDatabase();

        String CREATE_WEEKDAY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME     +   "(" +
                KEY_ID              +" INTEGER PRIMARY KEY,"+
                KEY_USER            +" TEXT,"+
                KEY_CURRENT_WEIGHT  +" NUMERIC,"+
                KEY_START_DATE      +" TEXT,"+
                KEY_PLAN_START     +" TEXT"+")";

        db.execSQL(CREATE_WEEKDAY_TABLE);

        Utilities.debugLog(getAllTableName().toString() + " JSDLKAD");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTableIfNotExist();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void createPlan(String tableName,String planName){
        String query = "CREATE TABLE " + tableName +    "(" +
                                                            KEY_ID                      +" INTEGER PRIMARY KEY,"+
                                                            KEY_PLAN_NAME               +" TEXT,"+      //0
                                                            KEY_START_WEEK              +" TEXT,"+      //1
                                                            KEY_END_WEEK                +" TEXT,"+      //2
                                                            KEY_WORKOUT_COMPLETED       +" NUMERIC,"+   //3
                                                            KEY_NUM_EXERCISE_COMPLETED  +" NUMERIC,"+   //4
                                                            KEY_NUM_REPS_COMPLETED      +" NUMERIC,"+   //5
                                                            KEY_MAX                     +" NUMERIC,"+   //6
                                                            KEY_MISSED_WORKOUT          +" NUMERIC,"+   //7
                                                            KEY_RATING                  +" NUMERIC"+    //8
                                                        ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        ContentValues values = new ContentValues();
        values.put(KEY_PLAN_NAME,planName);

        db.insert(tableName,null,values);
        db.close();
    }
    public void insertInfoIntoPlan(String tableName, ArrayList<String> progressInfo){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for(int i = 0; i < progressInfo.size() ; i++){
            values.put(KEYS[i], progressInfo.get(i));
        }

        db.insert(tableName, null, values);
        db.close();
    }//progressInfo structure, pos 0 = planName, pos 1 = start week; pos 2 = end week; pos 3 = workout completed; pos 4 = num exercise completed; pos 5 = num reps completed; pos 6 = max; pos 7 = missed workout; pos 8 = rating;
    public int getTableCount(String tableName){
        int count = 0;

        String countQuery = "SELECT * FROM " + tableName;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        count = cursor.getCount();
        cursor.close();
        return count;
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
    public ArrayList<String> getUserInfo() {
        ArrayList<String> rowStringList = new ArrayList<>();
        String rowString = "";

        String query = "SELECT * FROM user";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        rowStringList.add(cursor.getString(cursor.getColumnIndex(KEY_USER)));
        rowStringList.add(cursor.getString(cursor.getColumnIndex(KEY_CURRENT_WEIGHT)));
        rowStringList.add(cursor.getString(cursor.getColumnIndex(KEY_START_DATE)));
        rowStringList.add(cursor.getString(cursor.getColumnIndex(KEY_PLAN_START)));

        cursor.close();
        return rowStringList;
    }
    public void clearDatabase(){
    }//TODO clear user database
    public void deleteTable(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
    public void startPlan(boolean planStarted){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        ArrayList<String> temp = getUserInfo();
        value.put(KEY_USER,temp.get(0));
        value.put(KEY_CURRENT_WEIGHT,temp.get(1));
        value.put(KEY_START_DATE,temp.get(2));

        if(planStarted){
            value.put(KEY_PLAN_START, "yes");
        }else{
            value.put(KEY_PLAN_START, "no");
        }
        db.update(TABLE_NAME,value,"_id="+1,null);
    }//TODO
    public boolean planStarted(){
        SQLiteDatabase db = this.getReadableDatabase();

        return true;
    }

}
