package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.luxuan.stitcher.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

        imageView=(ImageView)findViewById(R.id.imageViewDisp);
        String path=getIntent().getStringExtra("pathkey");
        Log.d("Image", path);
        File file=new File(path);
        Bitmap bitmap=null;
        try{
            bitmap= BitmapFactory.decodeStream(new FileInputStream(file));
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }
}
