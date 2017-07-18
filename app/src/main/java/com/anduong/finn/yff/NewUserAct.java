package com.anduong.finn.yff;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Context;
import android.content.DialogInterface;

public class NewUserAct extends AppCompatActivity
{
    private Button yesBtn, noBtn;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);

        yesBtn = (Button) findViewById(R.id.new_yesBtn);
        noBtn =  (Button) findViewById(R.id.new_nosBtn);

        setButtonsListener();

        Utilities.setButtonClickColor(yesBtn, Color.GREEN);
        Utilities.setButtonClickColor(noBtn, Color.RED);

    }
    private void setButtonsListener(){

        yesBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewUserAct.this, UserSetterAct.class));
                Utilities.debugLog("Moving to UserSetterAct");
            }
        });

        noBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Let me know");
                dialogBuilder.setMessage("Open again when you are serious about working out");
                dialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        Utilities.debugLog("Exiting app");
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }
}
