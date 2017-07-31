package com.anduong.finn.yff;

import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.getCurrentDate;
import static com.anduong.finn.yff.Utilities.getReformatCurrentDate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by An Duong on 7/21/2017.
 */

public class GoalAct extends AppCompatActivity{
    private Button backBtn, confirmBtn;
    private EditText lbsEdit,currLbsEdit ,weeksNumEdit;
    private TextView planNameView;
    private static String planNameTxt;
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal);

        planNameView = (TextView) findViewById(R.id.goal_plan_name);
        backBtn = (Button) findViewById(R.id.goal_back_btn);
        confirmBtn = (Button) findViewById(R.id.goal_confirm_btn);
        lbsEdit = (EditText) findViewById(R.id.goal_lbs_num_edit);
        weeksNumEdit = (EditText) findViewById(R.id.goal_week_num_edit);
        currLbsEdit = (EditText) findViewById(R.id.goal_current_lbs_num_edit);

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
                createNewPlan();

                startActivity(intent);
                debugLog("user click confirmBtn. Moving to UserMainAct");
            }
        });
    }

    private void createNewPlan(){
        String planName = planNameTxt + "_" + getReformatCurrentDate();
        double goalLbs = Double.parseDouble(lbsEdit.getText().toString());
        int duration = Integer.parseInt(weeksNumEdit.getText().toString());
        String currentLbs = currLbsEdit.getText().toString();

        UserInfoPlanDBHandler userPlanDB = new UserInfoPlanDBHandler(context);
        DatabaseHandler db = new DatabaseHandler(context, planNameTxt);
        UserInfoDBHandler userDB = new UserInfoDBHandler(context);

        String map = "[";
        for(String tableName : db.getAllTableName()){
           for(String tableContent : db.getRowString(tableName)){
               map += "?.";
           }
        }
        map += "]";

        String planTable =  userPlanDB.startNewPlan(planName,goalLbs,map);
        userPlanDB.updateCurrentWeightAt(planTable,1,currentLbs);

        debugLog(planTable + " ASDIJ");
        //debugLog(userPlanDB.getRowString(planTable).toString() + " \n ADJA");

        userDB.updateCurrentPlanStartDate(getCurrentDate());
        userDB.updateCurrentPlan(planTable);
        userDB.updateCurrentPlanDuration(duration);
    }

}
