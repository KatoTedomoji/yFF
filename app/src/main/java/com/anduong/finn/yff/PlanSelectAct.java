package com.anduong.finn.yff;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by An Duong on 6/23/2017.
 */

public class PlanSelectAct extends AppCompatActivity {
    private ArrayList<Button> buttonList;
    private LinearLayout btnLayout;
    public String selectedPlanStr = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_select);
        btnLayout = (LinearLayout) findViewById(R.id.plan_select_btn_layout);
        buttonList = new ArrayList<Button>();

        for(int btnIndex = 0 ; btnIndex < btnLayout.getChildCount(); btnIndex++){
            Button btn = (Button) btnLayout.getChildAt(btnIndex);
            btn.setTranslationX(Utilities.getScreenWidth()-140);
            Utilities.setButtonClickColor(btn,Color.GREEN);
            buttonList.add(btn);
        }

        setListener();

    }
    private void setListener(){
        for(final Button btn : buttonList){
            btn.animate().setDuration(1000);
            btn.animate().translationXBy(-(Utilities.getScreenWidth()-140));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn.getText().toString().equalsIgnoreCase("custom")){
                        startActivity(new Intent(PlanSelectAct.this, PlanSetterAct.class));
                        Utilities.debugLog("Moving to UserMainACt");
                    }else{
                        startActivity(new Intent(PlanSelectAct.this, UserMainAct.class));
                        selectedPlanStr = btn.getText().toString();
                        Utilities.debugLog("Moving to UserMainACt");
                    }
                }
            });
        }
    }
}
