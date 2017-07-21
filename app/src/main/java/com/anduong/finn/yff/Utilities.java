package com.anduong.finn.yff;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by An Duong on 6/18/2017.
 * Purpose: useful utilities
 */

public class Utilities {
    static final Calendar calendar = Calendar.getInstance();
    public static String generateWeeksOf(int weekNum, int daysSeparate){
        String week;
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1 + daysSeparate );
        Date weekStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 6);
        Date weekEnd = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        week = "Week " + Integer.toString(weekNum);
        Utilities.debugLog("Created: "+ week);
        return week;
    }//for logging later
    public static int getCurrentMonday(){
        int mondayDate = 0;
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
        Date weekStart = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        mondayDate = Integer.parseInt(dateFormat.format(weekStart));
        return mondayDate;
    }// for logging later, to open pictureTaker on every monday onnce
    public static String getCurrentWeekDay(){
        Date date = Calendar.getInstance().getTime();
        return new SimpleDateFormat("EEEE",Locale.ENGLISH).format(date.getTime());
    }
    public static String getCurrentDate(){
        return new SimpleDateFormat("MM-dd-yyyy").format(new Date());
    }
    public static void setVisibleAndPop(Context context, View view){
        Animation popAnimate = AnimationUtils.loadAnimation(context, R.anim.pop_animation);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(popAnimate);
    }//pop
    public static void setVisibleAndFadeIn(Context context, View view){
        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadeInAnimation);
    }//fade in
    public static void setInvisibleAndSlideDownAnimation(Context context,View view){
        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        view.startAnimation(fadeInAnimation);
        view.setVisibility(View.INVISIBLE);
    }//slide down, only use to make it disappear
    public static void setVisibleAndSlideUpAnimation(Context context,View view){
        view.setVisibility(View.VISIBLE);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        view.startAnimation(fadeInAnimation);
    }//slide up
    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }//for positioning
    public static void setButtonClickColor(final Button btn, final int color){
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundColor(Color.BLACK);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundColor(color);
                }
                return false;
            }
        });
    }//set text and background color
    public static void setButtonTextClickColor(final Button btn, final int color){
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn.setTextColor(Color.BLACK);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setTextColor(color);
                }
                return false;
            }
        });
    }//set text color
    public static int debugLog(String logger){
        return Log.d("appDebug",logger);
    }//generate debug log for testing
    public static int debugLog( Exception e){
        return Log.d("appDebug","",e);
    } //generate debug log with exception for testing
}
