package com.anduong.finn.yff;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by NEW PC on 7/19/2017.
 */

public class DBHandler extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "mydata.db";
    private static final int DATABASE_VERSION = 1;

    private static String TABLE_NAME = "";//Plan name
    private static final String COLUMN_DAY = "DAY STRING";
    private static final String COLUMN_EXERCISE = "EXERCISE NAME";
    private static final String COLUMN_LBS = "AT LBS";
    private static final String COLUMN_REPS  = "FOR REPS";
    private static ArrayList<String> createTablesQueries;
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("+
                                                                    COLUMN_DAY +        " TEXT,"+
                                                                    COLUMN_EXERCISE +   " TEXT,"+
                                                                    COLUMN_LBS +        " NUMERIC"+
                                                                    COLUMN_REPS +       " NUMERIC"+
                                                                                ")";
    private static ArrayList<String> tableNames;

    public DBHandler(Context context, ArrayList<String> tableNames) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.tableNames = tableNames;
        createTablesQueries = new ArrayList<>();

        for(String table: tableNames){
            //createTablesQueries.add()
        }
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
