package com.luxuan.stitcher.stitcher.Beans;

import android.graphics.Bitmap;

public class DetailItem {

    private Bitmap bitmap;
    private String path;

    public DetailItem(){

    }

    public DetailItem(Bitmap map, String path){
        this.bitmap=bitmap;
        this.path=path;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getPath(){
        return path;
    }
}
