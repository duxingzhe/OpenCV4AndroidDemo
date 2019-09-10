package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.luxuan.stitcher.R;

import java.io.File;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private static String filePath="/mnt/sdcard/FirstPdf.pdf";
    private RecyclerView rvPictures;
    private ArrayList<Bitmap> bitmapLists;
    private File[] fileLists;
    private String string;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        string=getIntent().getStringExtra("folder");
        getSupportActionBar().setTitle(string);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

        bitmapLists=new ArrayList<>();

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                new PDFAsyncTask().execute();
            }
        });

        rvPictures=(RecyclerView)findViewById(R.id.rvPics);

        new LoadPhoto(string).execute();
    }
}
