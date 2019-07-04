package com.luxuan.answersheetscan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

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

    public static Bitmap resizeImage(Bitmap bitmap, int maxWidth, int maxHeight){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        Float newWidth;
        Float newHeight;
        if(width>maxWidth){
            newHeight=1.0F*maxWidth/width*height;
            newWidth=1.0F*maxWidth;
            if(newHeight>maxHeight){
                newWidth=maxHeight/newHeight*newWidth;
                newHeight=1.0F*maxHeight;
            }
        }else if(height>maxHeight){
            newWidth=1.0F*maxHeight/height*width;
            newHeight=1.0f*maxHeight;
            if(newWidth>maxWidth){
                newHeight=maxWidth/newWidth*newHeight;
                newWidth=1.0F*maxWidth;
            }
        }else{
            newWidth=1.0F*maxWidth;
            newHeight=1.0F*maxHeight;
        }
        float scaleWidth=newWidth/width;
        float scaleHeight=newHeight/height;
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    private static float getDensity(Context context){
        return context.getApplicationContext().getResources().getDisplayMetrics().density;
    }

    public static int dpToPx(Context context, Float dpValue){
        return (int)(dpValue*getDensity(context)+0.5F);
    }

    public static int pxToDp(Context context, Float pxValue){
        return (int)(pxValue/getDensity(context)+0.5F);
    }
}
