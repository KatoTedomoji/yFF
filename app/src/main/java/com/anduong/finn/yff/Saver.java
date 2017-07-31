package com.anduong.finn.yff;
import static com.anduong.finn.yff.Utilities.debugLog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteOpenHelper;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/*
 *  Created by An Duong on 6/13/2017.
 *  Purpose: writing data file to external storage
 */

public class Saver{
    static final File mainDir = new File(Environment.getExternalStorageDirectory(),"yFF");
    static final File dataDir = new File(mainDir.getAbsolutePath()+File.separator,"data");
    static final File planDir = new File(mainDir.getAbsoluteFile() + File.separator, "plan");
    static final File pictureDir = new File(mainDir,".Pictures");// hide pictures from Gallery

    private static ArrayList<String> planDataFiles = new ArrayList<>();
    public static HashMap<String, DatabaseHandler> dbMap = new HashMap<>();
    private static Context context1;

    public static ArrayList<String> getAllFileNameInDataDir(){
        ArrayList<String> fileNameList = new ArrayList<>();
        for(File file : planDir.listFiles()){
            String fileName = file.getName();
            int pos = fileName.lastIndexOf(".");
            if(pos > 0){
                fileName = fileName.substring(0,pos);
            }
            fileNameList.add(fileName);
        }
        return fileNameList;
    }
    private static void createDirIfNotExist(){
        mainDir.mkdir();
        dataDir.mkdir();
        planDir.mkdir();
        debugLog("asdasd");
    }
    public static void createUserDB(String userFileName){
        createDirIfNotExist();
        File userDB = new File((dataDir + File.separator), userFileName+".db");
        try{
            userDB.createNewFile();
        }catch (IOException e){
            debugLog(e);
        }
    }
    public static boolean createDBFile(String planName,Context context){
        context1 = context;
        createDataDB(planName);
        return true;
    }
    public static boolean addExerciseTo(String dataBaseFileName, String tableName, String exerciseInfo){
        DatabaseHandler temp = dbMap.get(dataBaseFileName);
        temp.addExercise(tableName,exerciseInfo);
        return true;
    }
    private static boolean createDataDB(String fileName){
        if(planDataFiles.size()>0){
            for(String plan : planDataFiles){
                if(!plan.equals(fileName)){
                    try{
                        File planDataFile = new File(planDir +File.separator,fileName+".db");
                        planDataFile.createNewFile();
                        planDataFiles.add(fileName);
                        DatabaseHandler db = new DatabaseHandler(context1,fileName);
                        dbMap.put(fileName,db);
                    }catch(IOException e){
                        debugLog(e);
                        return false;
                    }
                }
            }
        }else{
            try{
                File planDataFile = new File(planDir+"/",fileName+".db");
                planDataFile.createNewFile();
                planDataFiles.add(fileName);
                DatabaseHandler db = new DatabaseHandler(context1,fileName);
                dbMap.put(fileName,db);
            }catch(IOException e){
                debugLog(e);
                return false;
            }
        }
        return true;
    }
    public static int getTableCountFrom(String fileName, String tableName){
        DatabaseHandler db = dbMap.get(fileName);
        return db.getTableCount(tableName);
    }
    public static ArrayList<String> getRowFrom(String databaseName, String tableName){
        DatabaseHandler db = dbMap.get(databaseName);
        return db.getRowString(tableName);
    }

    public static void deleteAllFiles(){
        if(dataDir.exists()){
            File[] planDirFiles = dataDir.listFiles();
            for(File file : planDirFiles){
                file.delete();
            }
            debugLog("Deleted all files");
        }else{
            debugLog("dataDir do not exist");
        }

    }
    public static ArrayList<String> getAllTableNameFrom(String databaseFileName){
        DatabaseHandler db = dbMap.get(databaseFileName);
        Utilities.debugLog(db.getDatabaseName());
        return db.getAllTableName();
    }
//    public static File makePhotoFile(){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
//        String picFileStr = dateFormat.format((Calendar.getInstance()).getTime());
//        File photoFile = new File(Saver.pictureDir, (picFileStr + ".jpg"));
//        try{
//            if(!photoFile.exists()){
//                photoFile.createNewFile();
//            }else {
//                photoFile.delete();
//                photoFile.createNewFile();
//            }
//        }catch(IOException e){
//            Utilities.debugLog("Make photo file unsuccessful",e);
//        }
//        return photoFile;
//    }
//    public static Bitmap makePhotoVertical(Bitmap photo,File photoPATH){
//        try {
//            ExifInterface exif = new ExifInterface(photoPATH.getAbsolutePath());
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//            Matrix matrix = new Matrix();
//            if (orientation == 6) {
//                matrix.postRotate(90);
//            }
//            else if (orientation == 3) {
//                matrix.postRotate(180);
//            }
//            else if (orientation == 8) {
//                matrix.postRotate(270);
//            }
//            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true); // rotating bitmap
//            Utilities.debugLog("Photo rotate successful");
//        }
//        catch (Exception e) {
//            Utilities.debugLog("Cannot rotate photo",e);
//        }
//        return photo;
//    }
}
