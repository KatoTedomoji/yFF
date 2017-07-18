package com.anduong.finn.yff;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by An Duong on 6/11/2017.
 */

public class UserSetterAct extends AppCompatActivity {
    private Button nextBtn;
    private double userCurrWeight, userGoalWeight;
    private String userNameStr;
    private EditText userName,currWeight,goalWeight;
    private final Context context = this;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_user);

        userName = (EditText) (findViewById(R.id.nameTxt));
        currWeight = (EditText) (findViewById(R.id.currWeightTxt));
        goalWeight = (EditText) (findViewById(R.id.goalWeightTxt));
        nextBtn = (Button) findViewById(R.id.userSetterNextButton);

        nextBtn.animate().setDuration(1000);
        nextBtn.animate().translationXBy(-650);

        userName.setHint("Enter your name");
        currWeight.setHint("Enter number");
        goalWeight.setHint("Enter number");

        listenToNextBtn();

    }
    private void listenToNextBtn(){
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Checker.hasText(userName) && Checker.isNum(currWeight) && Checker.isNum(goalWeight)){
                    Utilities.setButtonClickColor(nextBtn,Color.GREEN);
                    userCurrWeight = Double.parseDouble(currWeight.getText().toString());
                    userGoalWeight = Double.parseDouble(goalWeight.getText().toString());
                    userNameStr = userName.getText().toString();
                    Utilities.debugLog("Recording user info");

                    user = new UserInfo(userNameStr,userCurrWeight);
                    user.setGoalWeight(userGoalWeight);
                    Saver.saveUserData(user, context);
                    Saver.saveSchedulesData(new ArrayList<WeekScheduler>(),context);
                    Utilities.debugLog("Saving new user");
                    Utilities.debugLog("Saving new schedule");

                    startActivity(new Intent(UserSetterAct.this, PlanSelectAct.class));
                    Utilities.debugLog("Moving to PlanSelectAct");
                }else{
                    Utilities.setButtonClickColor(nextBtn,Color.RED);
                }
            }
        });

    }
}