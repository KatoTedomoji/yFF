package com.anduong.finn.yff;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NEW PC on 7/19/2017.
 */

public class ExercisePlan {
    private String planName;
    private static final String[] weekdayName= {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private ArrayList<ArrayList<String>> weekdayPlanList;

    public ExercisePlan(String planName){
        this.planName = planName;

    }

    private HashMap createWeekdayMap(){
        HashMap<String,ArrayList<String>> weekdayMap = new HashMap<>();

        for(String dayName: weekdayName){
            weekdayMap.put(dayName,new ArrayList<String>());
        }
        return weekdayMap;
    }


}
