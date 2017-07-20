package com.anduong.finn.yff;

import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.setButtonClickColor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by An Duong on 6/23/2017.
 */

public class PlanSelectAct extends AppCompatActivity {
    private ArrayList<Button> buttonList;
    private LinearLayout btnLayout,planLayout;
    public String selectedPlanStr = "";
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_select);
        btnLayout = (LinearLayout) findViewById(R.id.plan_select_btn_layout);
        planLayout = (LinearLayout) findViewById(R.id.plan_select_plan_list);
        buttonList = new ArrayList<Button>();

        for(String fileName : Saver.getAllFileNameInDataDir()){
            planLayout.addView(createSelectItemView(fileName));
        }
        setupAnimationFor(planLayout, buttonList);
        setButtonListener();

    }
    private View createSelectItemView(String title){
        View inflatedLayout = getLayoutInflater().inflate(R.layout.plan_select_item,null);
        Button b = inflatedLayout.findViewById(R.id.plan_select_btn);
        b.setText(title);
        return inflatedLayout;
    }
    private void setupAnimationFor(LinearLayout layout, ArrayList<Button> buttonList){
        for(int btnIndex = 0 ; btnIndex < layout.getChildCount(); btnIndex++){
            LinearLayout outer = (LinearLayout) layout.getChildAt(btnIndex);
            Button btn = outer.findViewById(R.id.plan_select_btn);
            buttonList.add(btn);
        }
        buttonList.add((Button)btnLayout.findViewById(R.id.plan_select_custom_btn));
        for(Button button : buttonList){
         animateButton(button);
        }
    }
    private void animateButton(Button button){
        button.setTranslationX(Utilities.getScreenWidth()-140);
        setButtonClickColor(button,Color.GREEN);
    }
    private void setButtonListener(){
        for(final Button btn : buttonList){
            btn.animate().setDuration(1000);
            btn.animate().translationXBy(-(Utilities.getScreenWidth()-140));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn.getText().toString().equalsIgnoreCase("add more")){
                        startActivity(new Intent(PlanSelectAct.this, PlanSetterAct.class));
                        debugLog("Moving to PlanSetterAct");
                    }else{
                        selectedPlanStr = btn.getText().toString();

                        Intent intent = new Intent(PlanSelectAct.this, UserMainAct.class);
                        intent.putExtra("planTxt",selectedPlanStr);
                        startActivity(intent);
                        debugLog("Moving to UserMainACt");
                    }
                }
            });
        }
    }
}
