package com.luxuan.opencv4demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

import org.opencv.android.Utils;
import org.opencv.imgcodecs.Imgcodecs;

public class PhotoActivity extends AppCompatActivity {

    public static void startAction(Context context, String name){
        Intent intent=new Intent(context, PhotoActivity.class);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        PhotoView pvPhoto=findViewById(R.id.pv_photo);
        if(MainActivity.mTargetMat!=null){
            Bitmap targetBitmap=Bitmap.createBitmap(MainActivity.mTargetMat.width(), MainActivity.mTargetMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(MainActivity.mTargetMat, targetBitmap);
            pvPhoto.setImageBitmap(BitmapUtils.changeChannels(targetBitmap));
            Imgcodecs.imwrite(MainActivity.mBasePath+getIntent().getStringExtra("name")+".jpg", MainActivity.mTargetMat);
        }
    }
}
