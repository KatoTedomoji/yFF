package com.anduong.finn.yff;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by An Duong on 6/25/2017.
 */

public class WeekScheduler implements Serializable {
    private String planName;
    private int weekNum;
    private ArrayList<String> exerciseList;

    public WeekScheduler(String planName, int weekNum){
        this.planName = planName;
        this.weekNum = weekNum;
        exerciseList = new ArrayList<String>();
    }
    public String getPlanName(){
        return planName;
    }
    public String getWeekNum(){
        return "Week " + weekNum;
    }

    public void addExercise(String exerciseName){
        exerciseList.add(exerciseName);
    }

    public ArrayList<String> getExerciseList(){
        return exerciseList;
    }

}
