package com.luxuan.answersheetscan.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {

    private BitmapUtils(){

    }

    public static File bitmapToFile(Bitmap bitmap, String savePath, String saveName){
        File dirFile=new File(savePath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File targetFile=new File(savePath+saveName+".jpg");
        BufferedOutputStream bos=null;
        try{
            bos=new BufferedOutputStream(new FileOutputStream(targetFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return targetFile;
    }
}
