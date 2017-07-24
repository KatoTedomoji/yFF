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

    private static final String TABLE_NAME = "user";
    // User Info
    private static String KEY_ID = "id";
    private static final String KEY_USER = "user_name";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_CURRENT_PLAN = "current_plan_name";
    private static final String KEY_CURRENT_PLAN_DURATION = "current_plan_duration";
    private static final String KEY_START_WEIGHT = "start_weight";
    private static final String KEY_EXERCISE_COMPLETED = "exercise_completed";
    private static final String KEY_EXERCISE_MISSED = "exercise_missed";
    private static final String KEY_REPS_COMPLETED = "reps_completed";

    private static final String[] KEYS = {KEY_ID, KEY_USER, KEY_START_DATE, KEY_CURRENT_PLAN, KEY_CURRENT_PLAN_DURATION, KEY_START_WEIGHT,
                                            KEY_EXERCISE_COMPLETED, KEY_EXERCISE_MISSED, KEY_REPS_COMPLETED};

    private static String userName;
    private static double userCurrentWeight;

    public UserInfoDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTableIfNotExist(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void setUser(String userName, double userCurrentWeight){
        Saver.createUserDB(DATABASE_NAME);
        this.userName = userName;
        this.userCurrentWeight = userCurrentWeight;

        SQLiteDatabase db = this.getWritableDatabase();
        deleteTable(db);
        createUserTableIfNotExist(db);

        ContentValues values = new ContentValues();
        values.put(KEY_USER, userName);
        values.put(KEY_START_DATE, Utilities.getCurrentDate());
        values.put(KEY_CURRENT_PLAN, "none");
        values.put(KEY_CURRENT_PLAN_DURATION, 0);
        values.put(KEY_START_WEIGHT,userCurrentWeight);
        values.put(KEY_EXERCISE_COMPLETED, 0);
        values.put(KEY_EXERCISE_MISSED, 0);
        values.put(KEY_REPS_COMPLETED, 0);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    private void createUserTableIfNotExist(SQLiteDatabase db){

        String CREATE_WEEKDAY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME     +   "(" +
                KEY_ID                          +" INTEGER PRIMARY KEY,"+
                KEY_USER                        +" TEXT,"+
                KEY_START_DATE                  +" TEXT,"+
                KEY_CURRENT_PLAN                +" TEXT,"+
                KEY_CURRENT_PLAN_DURATION       +" NUMERIC,"+
                KEY_START_WEIGHT                +" NUMERIC,"+
                KEY_EXERCISE_COMPLETED          +" NUMERIC,"+
                KEY_EXERCISE_MISSED             +" NUMERIC,"+
                KEY_REPS_COMPLETED              +" NUMERIC"+")";

        db.execSQL(CREATE_WEEKDAY_TABLE);
    }
    private void deleteTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        createUserTableIfNotExist(db);
    }
    public ArrayList<String> getUserInfo() {
        ArrayList<String> rowStringList = new ArrayList<>();
        String query = "SELECT * FROM user";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToLast();

        for(String KEY : KEYS){
            rowStringList.add(cursor.getString(cursor.getColumnIndex(KEY)));
        }
        cursor.close();
        return rowStringList;
    }
    public void startNewPlan(String planName, int duration){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " +  TABLE_NAME +
                        " SET " +   KEY_CURRENT_PLAN +          "='" + planName+"',"+
                                    KEY_CURRENT_PLAN_DURATION + "='" + duration + "'"+
                        " WHERE " + KEY_ID +                    "=1";
        db.execSQL(query);
    }
    public boolean planHasStarted(){
        if(!getUserInfo().get(3).equals("none")){
            return true;
        }else{
            return false;
        }
    }

    public void updateCurrentPlan(String planName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_PLAN + "='" + planName+"' WHERE id =1";
        db.execSQL(query);
    }
    public void updateCurrentPlanDuration(int weeksLeft){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_PLAN_DURATION + "='" + weeksLeft+"' WHERE id =1";
        db.execSQL(query);
    }
    public void updateExerciseCompleted(int num){
        int exerciseCompleted = Integer.parseInt(get(KEY_EXERCISE_COMPLETED));
        int newExerciseCompleted = num + exerciseCompleted;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_PLAN_DURATION + "='" + newExerciseCompleted +"' WHERE id =1";
        db.execSQL(query);
    }
    public void updateExerciseMissed(int num){
        int exerciseMissed = Integer.parseInt(get(KEY_EXERCISE_MISSED));
        int newExerciseMissed = num + exerciseMissed;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_PLAN_DURATION + "='" + newExerciseMissed+"' WHERE id =1";
        db.execSQL(query);
    }
    public void updateRepsCompleted(int num){
        int repsCompleted = Integer.parseInt(get(KEY_REPS_COMPLETED));
        int newRepsCompleted = num + repsCompleted;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_PLAN_DURATION + "='" + newRepsCompleted+"' WHERE id =1";
        db.execSQL(query);
    }

    public String get(String columnName){
        ArrayList<String> content = getAllColumnContentFrom(TABLE_NAME);
        String output = "";
        for(int i = 0; i < KEYS.length; i ++){
            if(KEYS[i].equals(columnName)){
                output = content.get(i);
            }
        }

        return output;
    }
    public ArrayList<String> getAllColumnContentFrom(String tableName){
        ArrayList<String> rowStringList = new ArrayList<>();

        String query = "SELECT * FROM " +tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        cursor.moveToFirst();
        for(String KEY : KEYS){
            rowStringList.add(cursor.getString(cursor.getColumnIndex(KEY)));
        }
        return rowStringList;
    }
}
