package com.anduong.finn.yff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*
 *  Created by An Duong on 6/13/2017.
 *  Purpose: writing data file to external storage
 */

public class Saver {
    static final String scheduleFILENAME = "schedule.dat";
    static final String userFILENAME = "user.dat";
    static final String state = Environment.getExternalStorageState();
    static final File mainDir = new File(Environment.getExternalStorageDirectory() + File.separator +"yff");
    static final File dataDir = new File(mainDir,"Data");
    static final File pictureDir = new File(mainDir,".Pictures");// hide pictures from Gallery
    static final File dataFile = new File(dataDir,userFILENAME);
    static final File scheduleFile = new File(dataDir,scheduleFILENAME);
    static FileOutputStream fos;
    static ObjectOutputStream os;

    public static void saveUserData(UserInfo user, Context context){
        createIfFileDontExist();
        try{
            //open stream
             fos = context.openFileOutput(userFILENAME,Context.MODE_PRIVATE);
             os = new ObjectOutputStream(fos);

            //write
            os.writeObject(user);

            //close stream
            os.close();
            fos.close();
            Utilities.debugLog("Save user successful");
        }catch(IOException e){
            Utilities.debugLog("Cannot save user");
        }
    }
    public static void saveSchedulesData(ArrayList<WeekScheduler> weeks, Context context){
        createIfFileDontExist();
        try{
            //open stream
            FileOutputStream fos = context.openFileOutput(scheduleFILENAME,Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            //write
            os.writeObject(weeks);

            //close stream
            os.close();
            fos.close();
            Utilities.debugLog("Save user successful");
        }catch(IOException e){
            Utilities.debugLog("Cannot save user");
        }
    }
    public static UserInfo loadUserData(Context context){
        UserInfo user = null;
        try{
            //opening stream
            FileInputStream fileInputStream = context.openFileInput(userFILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            //load data
            user = (UserInfo) objectInputStream.readObject();

            //close stream
            objectInputStream.close();
            fileInputStream.close();
            Utilities.debugLog("Read user successful");
        }catch(IOException | ClassNotFoundException e){
            Utilities.debugLog("Cannot read user",e);
        }

        return user;
    }
    public static ArrayList<WeekScheduler> loadSchedulesData(Context context){
        ArrayList<WeekScheduler> weeks = null;
        try{
            //opening stream
            FileInputStream fileInputStream = context.openFileInput(scheduleFILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            //load data
            weeks = (ArrayList<WeekScheduler>) objectInputStream.readObject();

            //close stream
            objectInputStream.close();
            fileInputStream.close();
            Utilities.debugLog("Read weeks successful");
        }catch(IOException | ClassNotFoundException e){
            Utilities.debugLog("Cannot read weeks",e);
        }

        return weeks;
    }

    public static boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            Utilities.debugLog("Can write in external storage");
            return true;
        }else {
            Utilities.debugLog("Cannot write in external storage");
            return false;
        }
    }
    public static boolean isExternalStorageReadable(){
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Utilities.debugLog("Can only read in external storage");
            return true;
        } else {
            Utilities.debugLog("Can cannot read or write in external storage");
            return false;
        }
    }
    public static void createIfFileDontExist(){
        if(!dataFile.exists() |!scheduleFile.exists()| !mainDir.exists() | !pictureDir.exists() | !dataDir.exists()){
            try{
                Utilities.debugLog("File(s) may not exist, trying to create");
                mainDir.mkdir();
                dataDir.mkdir();
                pictureDir.mkdir();
                dataFile.createNewFile();
                scheduleFile.createNewFile();
                Utilities.debugLog("Creating directories and data file successfully");
            }catch(IOException e){
                Utilities.debugLog("Cannot create data file",e);
            }
        }
    }
    public static File makePhotoFile(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
        String picFileStr = dateFormat.format((Calendar.getInstance()).getTime());
        File photoFile = new File(Saver.pictureDir, (picFileStr + ".jpg"));
        try{
            if(!photoFile.exists()){
                photoFile.createNewFile();
            }else {
                photoFile.delete();
                photoFile.createNewFile();
            }
        }catch(IOException e){
            Utilities.debugLog("Make photo file unsuccessful",e);
        }
        return photoFile;
    }
    public static Bitmap makePhotoVertical(Bitmap photo,File photoPATH){
        try {
            ExifInterface exif = new ExifInterface(photoPATH.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true); // rotating bitmap
            Utilities.debugLog("Photo rotate successful");
        }
        catch (Exception e) {
            Utilities.debugLog("Cannot rotate photo",e);
        }
        return photo;
    }
}
