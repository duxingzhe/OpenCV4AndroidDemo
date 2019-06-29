package com.luxuan.answersheetscan.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast sToast;

    public static void showToast(Context context, String string){
        if(sToast!=null){
            sToast.cancel();
        }
        sToast=Toast.makeText(context.getApplicationContext(), string, Toast.LENGTH_SHORT);
        sToast.show();
    }
}
