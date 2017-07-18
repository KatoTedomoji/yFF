package com.anduong.finn.yff;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by An Duong on 6/11/2017.
 */

public class UserInfo implements Serializable{
    private double startWeight,goalWeight;
    private String name,dateStarted;
    private int workoutNum,missedWorkoutNum;
    private ArrayList<String> reasonsMissed;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat df = new SimpleDateFormat("MM.dd.yyyy");

    public UserInfo(String name, Double startWeight){
        this.name = name;
        this.startWeight = startWeight;
        dateStarted = df.format(c.getTime());
        workoutNum = 0;
        missedWorkoutNum = 0;
        reasonsMissed = new ArrayList<String>();
    }
    public String getName() {
        return name;
    }
    public double getBeginWeight() {
        return startWeight;
    }
    public double getTargetWeight() {
        return goalWeight;
    }
    public String getDateStarted(){
        return dateStarted;
    }
    public ArrayList<String> getReasonsMissed(){
        return reasonsMissed;
    }
    public int getWorkoutNum(){
        return workoutNum;
    }
    public int getMissedWorkoutNum(){
        return missedWorkoutNum;
    }
    public void addReasonsMissed(String reason){
        reason = df.format(c.getTime()) + "/t" + reason;
        reasonsMissed.add(reason);
        missedWorkoutNum++;
    }
    public void setGoalWeight(double goalWeight) {
        this.goalWeight = goalWeight;
    }
    public void addWorkoutNum(int i){
        workoutNum++;
    }
    public void addMissedWorkoutNum(int i){
        missedWorkoutNum++;
    }
}
