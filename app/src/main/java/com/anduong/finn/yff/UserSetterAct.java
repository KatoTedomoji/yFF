package com.anduong.finn.yff;

import static com.anduong.finn.yff.Saver.createDBFile;
import static com.anduong.finn.yff.Saver.createUserDB;
import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.getCurrentDate;
import static com.anduong.finn.yff.Utilities.setButtonClickColor;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by An Duong on 6/11/2017.
 */

public class UserSetterAct extends AppCompatActivity {
    private Button nextBtn;
    private double userCurrWeight;
    private String userNameStr;
    private EditText userName,currWeight;
    private final Context context = this;

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
                if(userName.getText().toString().trim().length() <= 0 ){
                    Toast.makeText(context,"What is your name?",Toast.LENGTH_SHORT);
                } else if(currWeight.getText().toString().trim().length() <= 0){
                    Toast.makeText(context,"What is your current weight?",Toast.LENGTH_SHORT);
                } else {

                    userCurrWeight = Double.parseDouble(currWeight.getText().toString());
                    userNameStr = userName.getText().toString();
                    debugLog("Recording user info");

                    UserInfoDBHandler userDB = new UserInfoDBHandler(context);

                    debugLog("Clearing all info");
                    Saver.deleteAllFiles();
                    userDB.setUser(userNameStr, userCurrWeight);

                    startActivity(new Intent(UserSetterAct.this, PlanSelectAct.class));
                    debugLog("Moving to PlanSelectAct");                }
            }
        });

    }
}