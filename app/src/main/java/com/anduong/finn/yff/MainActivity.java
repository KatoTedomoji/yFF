package com.anduong.finn.yff;

import static com.anduong.finn.yff.Saver.createUserDB;
import static com.anduong.finn.yff.Utilities.debugLog;
import static com.anduong.finn.yff.Utilities.setVisibleAndPop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    private ProgressBar loadingBar;
    private TextView accessYes, accessNo, permissionView;
    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessYes = (TextView) findViewById(R.id.access_granted_view);
        accessNo = (TextView) findViewById(R.id.access_denied_view);
        loadingBar = (ProgressBar) findViewById(R.id.main_loading_circle);
        permissionView= (TextView) findViewById(R.id.main_checkView);
        accessYes.setVisibility(View.GONE);
        accessNo.setVisibility(View.GONE);
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingBar.setVisibility(View.GONE);
                if(permissionGranted()){
                    setVisibleAndPop(context,accessYes);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Saver.dataDir.listFiles().length != 0){
                                UserInfoDBHandler userDB =  new UserInfoDBHandler(context);
                                if(!userDB.getUserInfo().isEmpty()){
                                    if(userDB.planHasStarted()){
                                        startActivity(new Intent(MainActivity.this, UserMainAct.class));
                                        debugLog("User exist and plan started, moving to UserMainAct");
                                    }else{
                                        startActivity(new Intent(MainActivity.this, PlanSelectAct.class));
                                        debugLog("User exist, but plan hasn't started, moving to PlanSelect");
                                    }
                                }else{
                                    startActivity(new Intent(MainActivity.this, NewUserAct.class));
                                    debugLog("User dont exist, moving to NewUserAct");
                                }
                            }else{
                                startActivity(new Intent(MainActivity.this, NewUserAct.class));
                            }
                        }
                    },1000);
                }else{
                    Utilities.setVisibleAndPop(context,accessNo);
                    ActivityCompat.requestPermissions(MainActivity.this, permissions,1);
                    debugLog("No permission, cant proceed");
                }
            }
        }, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // if access granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessNo.setVisibility(View.INVISIBLE);
                    permissionView.setText("Access Granted");
                    setVisibleAndPop(context,accessYes);

                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    private boolean permissionGranted(){
        boolean externalAccess =
                (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED);
        boolean cameraAccess =
                (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED);

        if(!externalAccess && !cameraAccess) {
            permissionView.setText("Access denied");
            return false;
        }else{
            permissionView.setText("Access granted");
            return true;
        }
    }
}