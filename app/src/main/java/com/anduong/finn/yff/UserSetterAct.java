package com.anduong.finn.yff;

import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.setButtonClickColor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.EditText;

/**
 * Created by An Duong on 6/11/2017.
 */

public class UserSetterAct extends AppCompatActivity {
    private Button nextBtn;
    private double userCurrWeight, userGoalWeight;
    private String userNameStr;
    private EditText userName,currWeight;
    private final Context context = this;
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_user);

        userName = (EditText) (findViewById(R.id.nameTxt));
        currWeight = (EditText) (findViewById(R.id.currWeightTxt));
        nextBtn = (Button) findViewById(R.id.userSetterNextButton);

        nextBtn.animate().setDuration(1000);
        nextBtn.animate().translationXBy(-650);

        userName.setHint("Enter your name");
        currWeight.setHint("Enter number");

        listenToNextBtn();

    }
    private void listenToNextBtn(){
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Checker.hasText(userName) && Checker.isNum(currWeight)){
                    setButtonClickColor(nextBtn,Color.GREEN);
                    userCurrWeight = Double.parseDouble(currWeight.getText().toString());
                    userNameStr = userName.getText().toString();
                    debugLog("Recording user info");

                    Saver.createDBFile("userDB",context);
                    UserInfoDBHandler userDB = new UserInfoDBHandler(context);
                    //userDB.deleteTable("user");

                    userDB.setUser(userNameStr, userCurrWeight);
                    debugLog(userDB.getAllTableName().toString());
                    debugLog(userDB.getUserInfo().toString());

                    userDB.startPlan(true);
                    debugLog(userDB.getUserInfo().toString());

                    startActivity(new Intent(UserSetterAct.this, PlanSelectAct.class));
                    debugLog("Moving to PlanSelectAct");
                }else{
                    setButtonClickColor(nextBtn,Color.RED);
                }
            }
        });

    }
}