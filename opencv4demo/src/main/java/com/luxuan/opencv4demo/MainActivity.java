package com.luxuan.opencv4demo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG="MainActivity";
    public static final String mBasePath= Environment.getExternalStorageDirectory()+ File.separator+"OpenCV4AndroidDemo"+File.separator;
    public static final String mImageName="00.jpg";
    public static final Mat mSrcMat;
    public static final Mat mTargetMat;

    private View mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initOpenCV();
        initView();
    }

    private void initView(){
        mProgress=findViewById(R.id.ll_progress);

        findViewById(R.id.btn_load_img).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                loadImg();
            }
        });

        findViewById(R.id.btn_rect_img).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                drawLine();
            }
        });

        findViewById(R.id.btn_line_img).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                drawRect();
            }
        });

        findViewById(R.id.btn_gray_img).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                doGray();
            }
        });

        findViewById(R.id.btn_invert_single_pixel_img).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                invertBySinglePixel();
            }
        });

        findViewById(R.id.btn_invert_all_pixel_img).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                invertByAllPixel();
            }
        });
    }
}
