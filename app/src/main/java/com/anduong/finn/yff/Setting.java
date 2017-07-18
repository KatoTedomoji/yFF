package com.anduong.finn.yff;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by An Duong on 6/20/2017.
 * Setting for UserMainAct
 */

public class Setting extends AppCompatActivity{
    private FloatingActionButton doneBtn;
    private RelativeLayout parentBox;
    private RelativeLayout childBox;
    private TextView settingTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        //init
        parentBox = (RelativeLayout) findViewById(R.id.setting_parent_box);
        childBox = (RelativeLayout) findViewById(R.id.setting_child_box);
    }

    private void hideBeforeTitle(boolean hide){
        if(hide){
            parentBox.setVisibility(View.GONE);
            childBox.setVisibility(View.GONE);
            doneBtn.setVisibility(View.INVISIBLE);
        }else{
            doneBtn.show();
            parentBox.setVisibility(View.VISIBLE);
            childBox.setVisibility(View.VISIBLE);
        }
    }
}
