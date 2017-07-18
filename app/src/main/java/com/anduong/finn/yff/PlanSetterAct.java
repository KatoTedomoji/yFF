package com.anduong.finn.yff;

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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;


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
        exerciseListScroll = (HorizontalScrollView) findViewById(R.id.plan_setter_exercise_scroll);
        loadingBar = (ProgressBar) findViewById(R.id.plan_setter_loading_bar);

        loadingBar.setVisibility(View.GONE);
        setupWeekdayParentsList();
        setupDayButtons();
        setupFooterButtons();
        setButtonClickColor(cancelBtn,Color.RED);
        setButtonClickColor(confirmBtn,Color.GREEN);
    }
    private void setupWeekdayParentsList(){
       for(int layoutIndex = 0; layoutIndex < 7; layoutIndex++){
           weekdayParentsList.add(new ArrayList<View>());
       }
    }//add 7 list represents exercises for Monday,Tues,Weds,etc...
    private void setupFooterButtons(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanSetterAct.this, UserMainAct.class));
                Utilities.debugLog("User cancel plan setter, moving to MainUser");
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanSetterAct.this, UserMainAct.class));
                Utilities.debugLog("User confirm plan setter, moving to MainUser");
            }
        });
    }//include cancel and confirm button
    private void setupDayButtons(){
        dayBtnList = new ArrayList<Button>();

        for(int btnIndex = 0; btnIndex < daysBtnParent.getChildCount();btnIndex++){
            dayBtnList.add((Button) daysBtnParent.getChildAt(btnIndex));
        }// for monday,tues,weds, etc..

        //for on open, monday is selected and exercise layout for monday=0
        highlightSelectedButton(dayBtnList.get(0),true);
        setupAddButtonFor(weekdayParentsList.get(0));

        for(final Button selectedDay: dayBtnList){
            selectedDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//Onclick switch to appropriate day layout from weekdayParentList
                    for(int buttonIndex = 0; buttonIndex < dayBtnList.size(); buttonIndex++){
                        if(dayBtnList.get(buttonIndex).equals(selectedDay)){
                            highlightSelectedButton(dayBtnList.get(buttonIndex),true);//invert color of selected button
                            loadingAnimation(buttonIndex);//also remove all current exerciseParentChildren
                            setupAddButtonFor(weekdayParentsList.get(buttonIndex));
                        }else{
                            highlightSelectedButton(dayBtnList.get(buttonIndex),false);
                        }
                    }
                }
            });
        }
    }//scrollable weekday buttons
    private void loadingAnimation(final int buttonIndex){
        addBtn.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
        exercisesParent.removeAllViews();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBar.setVisibility(View.GONE);
                ArrayList<View> weekdayParent = weekdayParentsList.get(buttonIndex);

                for(int childIndex = 0; childIndex < weekdayParent.size() ; childIndex++){
                    exercisesParent.addView(weekdayParent.get(childIndex));
                    setVisibleAndFadeIn(context, exercisesParent.getChildAt(childIndex));
                }//refill exerciseParent

               // hideFillerIfExerciseParentHasChildren();
                setVisibleAndFadeIn(context,addBtn);
                scrollToLastAddedToList(weekdayParent);
            }
        }, 200);
    }//remove add button for .2sec for loading animation
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
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View inflatedLayout = inflater.inflate(R.layout.add_exercise_layout,exercisesParent,false);
                exerciseList.add(inflatedLayout);//add to back-end list
                exercisesParent.addView(inflatedLayout);//add to visible view
                scrollToLastAddedToList(exerciseList);
                setVisibleAndPop(context,inflatedLayout);
                hideFillerIfExerciseParentHasChildren();
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
    private void hideFillerIfExerciseParentHasChildren(){
        if(exercisesParent.getChildCount() > 0){
            fillerBtn.setVisibility(View.GONE);
        }else {
            fillerBtn.setVisibility(View.VISIBLE);
        }
    }//hide filler button
}
