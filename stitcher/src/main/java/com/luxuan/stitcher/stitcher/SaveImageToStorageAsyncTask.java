package com.luxuan.stitcher.stitcher;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class SaveImageToStorageAsyncTask extends AsyncTask<String ,Void, Bitmap> {

    String name=String.valueOf(System.currentTimeMillis()/1000);
    String nameNew="Images";

    String val=name;
    Bitmap bitmapImage;

    public SaveImageToStorageAsyncTask(String val, Bitmap bitmapImage){
        this.nameNew=val;
        this.bitmapImage=bitmapImage;
    }

    @Override
    public Bitmap doInBackground(String... params){
        File directory=new File("/sdcard/DcoumentScanner/", "ScannedImage");

        if(!directory.exists()){
            directory.mkdirs();
            File dirThumb=new File(Environment.getExternalStorageDirectory(), "DocumentScanner/thumbnails");
            if(!dirThumb.exists()){
                dirThumb.mkdirs();
            }

            File thumbPath=new File(dirThumb, nameNew+".jpg");

            try{
                FileOutputStream fosThumb=new FileOutputStream(thumbPath);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fosThumb);
                fosThumb.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        name=String.valueOf(System.currentTimeMillis());
        String ts=name;
        File imagePath=new File(directory, ts+".jpg");

        FileOutputStream fos;

        try{
            fos=new FileOutputStream(imagePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG,100, fos);
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void onPostExecute(String result){

    }
}
