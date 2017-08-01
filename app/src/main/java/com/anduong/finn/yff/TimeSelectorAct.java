package com.anduong.finn.yff;

import static com.anduong.finn.yff.Utilities.debugLog;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

/**
 * Created by aduong on 7/31/17.
 */

public class TimeSelectorAct extends AppCompatActivity {
    private Button cancelBtn, confirmBtn;
    private TextView titleView;
    private TimePicker timePicker;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_selector_layout);

        titleView = (TextView) findViewById(R.id.time_selector_text);
        timePicker = (TimePicker) findViewById(R.id.time_selector_picker);
        cancelBtn = (Button) findViewById(R.id.time_selector_cancel_btn);
        confirmBtn = (Button) findViewById(R.id.time_selector_confirm_btn);

        setTitle();
        listenToFooterButtons();
    }
    private void setTitle(){
        Intent intent = getIntent();
        title = intent.getStringExtra("week");
        titleView.setText(title + " Timer");
    }
    private void listenToFooterButtons(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimeSelectorAct.this, UserMainAct.class));
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                debugLog("Alarm set at "+timePicker.getHour() + ":" +timePicker.getMinute() + " for the " + title);
                startActivity(new Intent(TimeSelectorAct.this, UserMainAct.class));
            }
        });//TODO take info from time picker and write in db
    }
}
