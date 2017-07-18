package com.anduong.finn.yff;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by An Duong on 6/16/2017.
 * Purpose: open camera to take picture and save it to PATH
 */

public class PictureTaker extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private Button nextButton;
    private File photoFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_taker);

        Utilities.debugLog("Starting Camera to take picture");
        takePictureAndSave();

        imageView = (ImageView)this.findViewById(R.id.imageView);
        nextButton = (Button) this.findViewById(R.id.imageViewButton);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PictureTaker.this, UserMainAct.class));
                Utilities.debugLog("Moving to UserMainAct");
            }
        });
    }
    private void takePictureAndSave(){
        photoFile = Saver.makePhotoFile();

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    //set picture from camera intent to imageView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imageView.setImageBitmap(Saver.makePhotoVertical(photo, photoFile));
        }
    }


}
