package com.luxuan.stitcher.stitcher.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Utils {

    private Context mContext;

    private static int RESULT_LOAD_IMAGE=1;

    public Utils(Context context){
        mContext=context;
    }

    private Utils(){

    }

    public static Uri getUri(Context context, Bitmap bitmap){
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path= MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap=MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public ArrayList<String> getFilePaths(){
        ArrayList<String> filePaths=new ArrayList<>();

        int m=0;
        File directory=new File("/sdcard/", "Batch");
        SharedPreferences pref=mContext.getSharedPreferences("CameraPref", Context.MODE_PRIVATE);
        String k=pref.getString("Count", null);
        try{
            m=Integer.parseInt(k);
        }catch(Exception e){
            e.printStackTrace();
            Log.i("check m", m+"");
        }

        Log.i("test dir", directory.toString());

        if(directory.isDirectory()){
            File[] listFiles=directory.listFiles();

            if(listFiles.length>0){
                for(int i=0;i<listFiles.length;i++){
                    int j=listFiles.length-m;
                    if(i>=j){
                        String filePath=listFiles[i].getAbsolutePath();

                        if(isSupportedFile(filePath)){
                            filePaths.add(filePath);

                            filePaths.get(filePaths.size()-1);
                        }
                    }
                }
            }else{
                Toast.makeText(mContext, " is empty. Please load some images in it !", Toast.LENGTH_LONG).show();
            }
        }else{
            AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
            alert.setTitle("Error!");
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        return filePaths;
    }

    private boolean isSupportedFile(String filePath){
        String extension=filePath.substring((filePath.lastIndexOf(".")+1), filePath.length());

        if(ScanConstants.FILE_EXTENSION.contains(extension.toLowerCase(Locale.getDefault()))) {
            return true;
        } else {
            return false;
        }
    }

    public int getScreenWidth(){
        int columnWidth;
        WindowManager wm=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();

        Point point=new Point();
        try{
            display.getSize(point);
        }catch(NoSuchMethodError ignore){
            point.x=display.getWidth();
            point.y=display.getHeight();
        }
        columnWidth=point.x;
        return columnWidth;
    }
}
