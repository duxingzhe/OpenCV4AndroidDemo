package com.luxuan.stitcher.stitcher.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Adapter.GridViewImageAdapter;
import com.luxuan.stitcher.stitcher.Util.Utils;

import java.io.File;
import java.util.ArrayList;

public class GridViewActivity extends AppCompatActivity {

    private Utils utils;
    private ArrayList<String> imagePaths=new ArrayList<>();
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

        gridView=(GridView)findViewById(R.id.grid_view);

        utils=new Utils(this);

        initializeGridLayout();

        imagePaths=utils.getFilePaths();
        mAdapter=new GridViewImageAdapter(GridViewActivity.this, imagePaths, columnWidth);

        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                File externalFile=new File(imagePaths.get(position));
                Log.i("search1", externalFile.toString());

                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig= Bitmap.Config.ARGB_8888;
                Bitmap bitmap=BitmapFactory.decodeFile(String.valueOf(externalFile),options);

                try{
                    if(bitmap!=null){
                        Uri uri=Utils.getUri(GridViewActivity.this, bitmap);
                        bitmap.recycle();
                        Log.i("GRid test uri", uri.toString());

                        Intent intent=new Intent(GridViewActivity.this, PolygonViewScreenActivity.class);
                        intent.putExtra("image2",uri);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(GridViewActivity.this, "Failed to Capture the picture. kindly Try Again:", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){

                }
            }
        });
    }
}
