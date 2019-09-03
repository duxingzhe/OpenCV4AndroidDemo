package com.luxuan.stitcher.stitcher.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Adapter.DocsAdapter;
import com.luxuan.stitcher.stitcher.Beans.DocItem;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerView rvDocs;
    private Uri fielUri, selectedImage;
    private ArrayList<DocItem>  iPostParams;
    private DocsAdapter adapter;
    private NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new CameraButtonClickListener());

        rvDocs=(RecyclerView)findViewById(R.id.rvDocs);
        iPostParams=new ArrayList<DocItem>();

        Filewalker fw=new Filewalker();
        String dirpath= Environment.getExternalStorageDirectory();
        File reader=new File(dirpath, "DocumentScanner");
        fw.walk(reader);

        adapter=new DocsAdapter(this, iPostParams);
        rvDocs.setAdapter(adapter);
        rvDocs.setLayoutManager(new GridLayoutManager(this, 2));
    }
}
