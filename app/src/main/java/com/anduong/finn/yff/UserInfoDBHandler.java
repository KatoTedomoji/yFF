package com.anduong.finn.yff;

/**
 * Created by an duong on 7/21/17.
 */
import static com.anduong.finn.yff.Utilities.debugLog;

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
    static String KEY_ID = "id";
    static final String KEY_USER = "user_name";
    static final String KEY_START_DATE = "start_date";
    static final String KEY_CURRENT_PLAN_START_DATE = "current_plan_start_date";
    static final String KEY_CURRENT_PLAN = "current_plan_name";
    static final String KEY_CURRENT_PLAN_DURATION = "current_plan_duration";
    static final String KEY_START_WEIGHT = "start_weight";
    static final String KEY_EXERCISE_COMPLETED = "exercise_completed";
    static final String KEY_EXERCISE_MISSED = "exercise_missed";
    static final String KEY_REPS_COMPLETED = "reps_completed";
    static final String KEY_PR = "personal_records";
    static final String KEY_TIMER = "timer";
    static final String KEY_TIMER_INFO = "timer_info";

    static final String[] KEYS = {KEY_ID, KEY_USER, KEY_START_DATE,KEY_CURRENT_PLAN_START_DATE, KEY_CURRENT_PLAN, KEY_CURRENT_PLAN_DURATION, KEY_START_WEIGHT,
                                            KEY_EXERCISE_COMPLETED, KEY_EXERCISE_MISSED, KEY_REPS_COMPLETED, KEY_PR, KEY_TIMER, KEY_TIMER_INFO};

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
        values.put(KEY_CURRENT_PLAN_START_DATE, "");
        values.put(KEY_CURRENT_PLAN, "none");
        values.put(KEY_CURRENT_PLAN_DURATION, 0);
        values.put(KEY_START_WEIGHT,userCurrentWeight);
        values.put(KEY_EXERCISE_COMPLETED, 0);
        values.put(KEY_EXERCISE_MISSED, 0);
        values.put(KEY_REPS_COMPLETED, 0);
        values.put(KEY_PR, "");
        values.put(KEY_TIMER, "off");
        values.put(KEY_TIMER_INFO,"");

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    private void createUserTableIfNotExist(SQLiteDatabase db){

        String CREATE_WEEKDAY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME     +   "(" +
                KEY_ID                          +" INTEGER PRIMARY KEY,"+
                KEY_USER                        +" TEXT,"+
                KEY_START_DATE                  +" TEXT,"+
                KEY_CURRENT_PLAN_START_DATE     +" TEXT,"+
                KEY_CURRENT_PLAN                +" TEXT,"+
                KEY_CURRENT_PLAN_DURATION       +" NUMERIC,"+
                KEY_START_WEIGHT                +" NUMERIC,"+
                KEY_EXERCISE_COMPLETED          +" NUMERIC,"+
                KEY_EXERCISE_MISSED             +" NUMERIC,"+
                KEY_REPS_COMPLETED              +" NUMERIC,"+
                KEY_PR                          +" TEXT,"+
                KEY_TIMER                       +" TEXT,"+
                KEY_TIMER_INFO                  +" TEXT"+
                ")";

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
        debugLog(cursor.getPosition()+" is cursor position");

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
        if(!getUserInfo().get(4).equals("none")){
            return true;
        }else{
            return false;
        }
    }

    public void updateCurrentPlanStartDate(String date){
        date = date.replaceAll("-","");
        debugLog(date +" SAD");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_CURRENT_PLAN_START_DATE + "='" + date+"' WHERE id =1";
        db.execSQL(query);
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
    public void updateTimerStatus(boolean on){
        String status = "";
        if(on){
            status = "on";
        }else{
            status = "off";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_TIMER + "='" + status +"' WHERE id =1";
        db.execSQL(query);
    }
    public void updateTimerInfo(String info){
        String currentInfo = get(KEY_TIMER_INFO) ;
        String newInfo = currentInfo + "," + info;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_TIMER_INFO + "='" + newInfo +"' WHERE id =1";
        db.execSQL(query);
    }

    public boolean getTimerStatus(){
        if(get(KEY_TIMER).equalsIgnoreCase("on")){
            return true;
        }else{
            return false;
        }
    }
    public String get(String columnName){
        ArrayList<String> content = getAllColumnContentFrom(TABLE_NAME);
        String output = "";
        for(int i = 0; i < KEYS.length; i ++){
            if(KEYS[i].equals(columnName)){
                output = content.get(i);
            }
        }
        String[] temp = output.split("-");
        return temp[0];
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

    public int getTableCount(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                c.moveToNext();
            }
        }
        return arrTblNames.size();
    }
    public ArrayList<String> getAllTableNames(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                c.moveToNext();
            }
        }

        return arrTblNames;
    }
}
