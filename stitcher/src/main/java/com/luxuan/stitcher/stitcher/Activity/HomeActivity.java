package com.luxuan.stitcher.stitcher.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.View;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Adapter.DocsAdapter;
import com.luxuan.stitcher.stitcher.Beans.DocItem;
import com.luxuan.stitcher.stitcher.Util.ScanConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
        String dirpath= Environment.getExternalStorageDirectory().toString();
        File reader=new File(dirpath, "DocumentScanner");
        fw.walk(reader);

        adapter=new DocsAdapter(this, iPostParams);
        rvDocs.setAdapter(adapter);
        rvDocs.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public class Filewalker{

        public void walk(File root){
            iPostParams=new ArrayList<>();
            DocItem postEmail=new DocItem("Dummy Doc", "2019/09/16",null);
            iPostParams.add(postEmail);

            Calendar c= Calendar.getInstance();
            SimpleDateFormat df=new SimpleDateFormat("dddd/MM/yy");
            String formattedDate=df.format(c.getTime());

            File[] list=root.listFiles();
            if(list!=null){
                for(File f : list){
                    if(f.isDirectory()&&!(f.getName().equals("thumbnails"))){
                        Log.d("", "Dir: "+f.getAbsoluteFile());
                        postEmail=null;
                        Bitmap bitmap=null;
                        File file=new File(Environment.getExternalStorageDirectory(), "/DocumentScanner/thumbnails/"+f.getName()+".jpg");

                        try{
                            bitmap= BitmapFactory.decodeStream(new FileInputStream(file));
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                        postEmail=new DocItem(f.getName(), formattedDate, bitmap);
                        iPostParams.add(postEmail);
                    }else{
                        Log.d("", "File: "+f.getAbsoluteFile());
                    }
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        navigationView.setCheckedItem(0);
        Filewalker fw=new Filewalker();
        String dirpath=Environment.getExternalStorageDirectory().toString();
        File reader=new File(dirpath, "DocumentScanner");
        fw.walk(reader);

        adapter=new DocsAdapter(this, iPostParams);
        rvDocs.setAdapter(adapter);
        rvDocs.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private class CameraButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            openCamera();
        }
    }

    private class GalleryClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            openMediaContent();
        }
    }

    public void openMediaContent(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    public void openCamera(){
        Intent intent=new Intent(this, CameraScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("", "onActivityResult"+resultCode);
        Bitmap bitmap=null;
        if(resultCode== Activity.RESULT_OK){
            try{
                switch(requestCode){
                    case ScanConstants.PICKFILE_REQUEST_CODE:
                        selectedImage=data.getData();
                        Intent intent=new Intent(this, PolygonViewScreenActivity.class);
                        intent.putExtra("imageTest1", selectedImage);
                        Log.i("test null", selectedImage.toString());
                        startActivity(intent);
                        break;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
