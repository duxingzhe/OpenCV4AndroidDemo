package com.luxuan.stitcher.stitcher;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class SaveImageToStorageAsyncTask extends AsyncTask<String ,Void, Bitmap> {

    String name=String.valueOf(System.currentTimeMillis()/1000);
    String nameNew="Images";

    String val=name;
    Bitmap bitmapImage;

    public SaveImageToStorageAsyncTask(String val, Bitmap bitmapImage){
        this.nameNew=val;
        this.bitmapImage=bitmapImage;
    }
}
