package com.anduong.finn.yff;

import android.widget.EditText;

/**
 * Created by An Duong on 6/13/2017.
 */

public class Checker {
    public static boolean isNum(EditText num){
        try{
            double dec = Double.parseDouble(num.getText().toString());
        }catch(NumberFormatException e){
            Utilities.debugLog("User did not enter a valid double",e);
            return false;
        }
        return true;
    }

    public static boolean hasText(EditText text){
        if(text.getText().toString().trim().length() == 0) {
            Utilities.debugLog("User did not enter text");
            return false;
        }
        return true;
    }

    //need code
    public static boolean deviceCameraSupport(){
       return true;
    }
}
