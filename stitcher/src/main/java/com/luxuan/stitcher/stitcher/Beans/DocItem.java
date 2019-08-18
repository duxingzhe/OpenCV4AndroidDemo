package com.luxuan.stitcher.stitcher.Beans;

import android.graphics.Bitmap;

public class DocItem {

    private String name, timeStamp;
    private Bitmap bitmap;

    public DocItem(){

    }

    public DocItem(String name, String timeStamp, Bitmap bitmap){
        this.name=name;
        this.timeStamp=timeStamp;
        this.bitmap=bitmap;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp=timeStamp;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
