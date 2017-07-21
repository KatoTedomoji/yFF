package com.anduong.finn.yff;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.solver.Goal;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by An Duong on 7/21/2017.
 */

public class GoalAct extends AppCompatActivity{
    private Button backBtn, confirmBtn;
    private EditText lbsEdit, weeksNumEdit;
    private TextView planNameView;
    private static String planNameTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal);

        planNameView = (TextView) findViewById(R.id.goal_plan_name);
        backBtn = (Button) findViewById(R.id.goal_back_btn);
        confirmBtn = (Button) findViewById(R.id.goal_confirm_btn);
        lbsEdit = (EditText) findViewById(R.id.goal_lbs_num_edit);
        weeksNumEdit = (EditText) findViewById(R.id.goal_week_num_edit);

        setPlanNameViewTxt();
        setOnclickFor(backBtn,confirmBtn);
    }

    private void setPlanNameViewTxt(){
        Intent intent = getIntent();
        planNameTxt = intent.getExtras().getString("planTxt");

        planNameView.setText(planNameTxt.toUpperCase());
    }
    private void setOnclickFor(Button backBtn, Button confirmBtn){
        Utilities.setButtonClickColor(backBtn, Color.GREEN);
        Utilities.setButtonClickColor(confirmBtn, Color.GREEN);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalAct.this, UserMainAct.class);
                intent.putExtra("planTxt",planNameTxt);
                startActivity(intent);
            }
        });
    }
}
