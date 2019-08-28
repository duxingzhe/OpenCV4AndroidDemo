package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Util.Utils;

import java.util.ArrayList;

public class GridViewActivity extends AppCompatActivity {

    private Utils utils;
    private ArrayList<String> imagePath=new ArrayList<>();
    private GridViewImageAdapter mAdapter;
    private GridView gridView;
    private int columnWidth;
    private ArrayList<String> filePaths=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Images");
        setTitleColor(Color.WHITE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
    }
}
