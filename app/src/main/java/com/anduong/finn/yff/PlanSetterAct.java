package com.anduong.finn.yff;

import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.setVisibleAndPop;
import static com.anduong.finn.yff.Utilities.setVisibleAndFadeIn;
import static com.anduong.finn.yff.Utilities.setButtonClickColor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by An Duong on 6/16/2017.
 */

public class PlanSetterAct extends AppCompatActivity {
    private Button cancelBtn, confirmBtn,addBtn,fillerBtn;
    private LinearLayout daysBtnParent,exercisesParent;
    private ArrayList<Button> dayBtnList;
    private ArrayList<ArrayList<View>> weekdayParentsList;
    private Context context = this;
    private HorizontalScrollView exerciseListScroll;
    private ProgressBar loadingBar;
    private EditText planNameView;
    private HashMap<String, ArrayList<String>> weekMap;
    private static String[] TABLE_NAMES = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_setter);

        cancelBtn = (Button) findViewById(R.id.plan_setter_cancel_btn);
        confirmBtn = (Button) findViewById(R.id.plan_setter_confirm_btn);
        fillerBtn = (Button) findViewById(R.id.plan_setter_filler_btn);
        addBtn = (Button) findViewById(R.id.plan_setter_add_btn);
        daysBtnParent = (LinearLayout) findViewById(R.id.plan_setter_days_btn_parent);
        exercisesParent = (LinearLayout) findViewById(R.id.plan_setter_exercise_list);
        weekdayParentsList = new ArrayList<>();
        weekMap = new HashMap<>();
        exerciseListScroll = (HorizontalScrollView) findViewById(R.id.plan_setter_exercise_scroll);
        loadingBar = (ProgressBar) findViewById(R.id.plan_setter_loading_bar);
        planNameView = (EditText) findViewById(R.id.plan_setter_plan_edit);

        loadingBar.setVisibility(View.GONE);
        setupWeekdayParentsListFor(weekdayParentsList);
        setupDayButtons();
        setupFooterButtons(cancelBtn, confirmBtn);
    }
    private void setupWeekdayParentsListFor(ArrayList list){
       for(int layoutIndex = 0; layoutIndex < 7; layoutIndex++){
           list.add(new ArrayList<View>());
       }
    }//add 7 list represents exercises for Monday,Tues,Weds,etc...
    private void setupDayButtons(){
        dayBtnList = new ArrayList<Button>();

        for(int btnIndex = 0; btnIndex < daysBtnParent.getChildCount();btnIndex++){
            dayBtnList.add((Button) daysBtnParent.getChildAt(btnIndex));
        }// for monday,tues,weds, etc..

        //for on open, monday is selected and exercise layout for monday=0
        highlightSelectedButton(dayBtnList.get(0),true);
        setupAddButtonFor(weekdayParentsList.get(0));
        hideFillerIfExerciseParentHasChildren(weekdayParentsList.get(0));

        for(final Button selectedDay: dayBtnList){
            selectedDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//Onclick switch to appropriate day layout from weekdayParentList
                    for(int buttonIndex = 0; buttonIndex < dayBtnList.size(); buttonIndex++){
                        if(dayBtnList.get(buttonIndex).equals(selectedDay)){
                            ArrayList<View> al = weekdayParentsList.get(buttonIndex);
                            highlightSelectedButton(dayBtnList.get(buttonIndex),true);//invert color of selected button
                            loadingAnimation(weekdayParentsList.get(buttonIndex));//also remove all current exerciseParentChildren
                            setupAddButtonFor(weekdayParentsList.get(buttonIndex));
                        }else{
                            highlightSelectedButton(dayBtnList.get(buttonIndex),false);
                        }
                    }
                }
            });
        }
    }//scrollable weekday buttons
    private void loadingAnimation(final ArrayList<View> exerciseList){
        addBtn.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
        exercisesParent.removeAllViews();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBar.setVisibility(View.GONE);
                for(int childIndex = 0; childIndex < exerciseList.size() ; childIndex++){
                    exercisesParent.addView(exerciseList.get(childIndex));
                    setVisibleAndFadeIn(context, exercisesParent.getChildAt(childIndex));
                }//refill exerciseParent

                setVisibleAndFadeIn(context,addBtn);
                scrollToLastAddedToList(exerciseList);
            }
        }, 200);
    }//loading animation handling
    private void highlightSelectedButton(Button button, boolean isSelected){
        if(isSelected){
            button.setTextColor(Color.WHITE);
            button.setBackgroundColor(Color.BLACK);
        }else{
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);
        }
    }// work exclusively with setupDayButtons()
    private void setupAddButtonFor(final ArrayList<View> exerciseList){
        hideFillerIfExerciseParentHasChildren(exerciseList);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View inflatedLayout = inflater.inflate(R.layout.add_exercise_layout,exercisesParent,false);
                exerciseList.add(inflatedLayout);//add to back-end list
                exercisesParent.addView(inflatedLayout);//add to visible view
                scrollToLastAddedToList(exerciseList);
                setVisibleAndPop(context,inflatedLayout);
                hideFillerIfExerciseParentHasChildren(exerciseList);
            }
        });
    }
    private void scrollToLastAddedToList(final ArrayList<View> list){
        exerciseListScroll.post(new Runnable() {
            @Override
            public void run() {
                int scrollTo = 0;
                final int count = list.size();
                for(int i = 0 ; i <count ; i ++){
                    scrollTo += 999999;//increment to scroll to last view
                }
                exerciseListScroll.scrollTo(scrollTo,0);

            }
        });
    }// work exclusively with setupAddButtonFor
    private void hideFillerIfExerciseParentHasChildren(ArrayList<View> parent){
        if(parent.size() > 0){
            fillerBtn.setVisibility(View.GONE);
        }else {
            fillerBtn.setVisibility(View.VISIBLE);
        }
    }//hide filler button

    private void setupHashMap(String planName){
        for(int parentIndex = 0; parentIndex < weekdayParentsList.size(); parentIndex ++){
            for(View child : weekdayParentsList.get(parentIndex)){
                LinearLayout childLayout = (LinearLayout) child;

                LinearLayout childRepsLayout = (LinearLayout) childLayout.getChildAt(0);
                EditText childRepsTxt = (EditText) childRepsLayout.getChildAt(0);

                EditText childExerciseTxt = (EditText) childLayout.getChildAt(1);

                LinearLayout childLbsLayout = (LinearLayout) childLayout.getChildAt(2);
                EditText childLbsTxt = (EditText) childLbsLayout.getChildAt(1);

                String exerciseInfoTxt = childExerciseTxt.getText() + "," + childLbsTxt.getText()+ "," +childRepsTxt.getText() ;
                Saver.addExerciseTo(planName,TABLE_NAMES[parentIndex], exerciseInfoTxt);
            }
        }

        //debugLog(weekMap.toString());
    }
    private void setupFooterButtons(Button cancel, final Button confirm){
        setButtonClickColor(cancel,Color.RED);
        //setButtonClickColor(confirm,Color.GREEN);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanSetterAct.this, PlanSelectAct.class));
                Utilities.debugLog("User cancel plan setter, moving to MainUser");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String planName = planNameView.getText().toString();
                ArrayList<String> fileNameList = Saver.getAllFileNameInDataDir();

                if(!fileNameList.contains(planName) && !planName.equals("") && !planName.equals(null)){
                    Utilities.setButtonClickColor(confirm,Color.GREEN);

                    Saver.createDBFile(planName,context);
                    setupHashMap(planName);

                    startActivity(new Intent(PlanSetterAct.this, PlanSelectAct.class));
                    Utilities.debugLog("User confirm plan setter, moving to PlanSelectAct");
                }else{
                    Utilities.setButtonClickColor(confirm,Color.RED);
                }
            }
        });
    }//moving back to UserMain
}
