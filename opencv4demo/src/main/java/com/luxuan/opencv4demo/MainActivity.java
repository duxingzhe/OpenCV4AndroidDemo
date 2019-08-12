package com.luxuan.opencv4demo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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

        findViewById(R.id.btn_avg_blur).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                avgBlur();
            }
        });

        findViewById(R.id.btn_mid_blur).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                midBlur();
            }
        });

        findViewById(R.id.btn_custom_blur).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                customBlur();
            }
        });

        findViewById(R.id.btn_gaussian_blur).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                gaussianBlur();
            }
        });
    }

    private void gaussianBlur(){
        if(resetMat()){
            showProgress();
            ThreadUtils.runOnSubThread(new Runnable(){

                @Override
                public void run(){
                    Imgproc.GaussianBlur(mSrcMat, mTargetMat, new Size(), 50F, 5F);
                    showResult("高斯模糊");
                }
            });
        }
    }

    private void midBlur(){
        if(resetMat()){
            showProgress();
            ThreadUtils.runOnSubThread(new Runnable(){

                @Override
                public void run(){
                    Imgproc.medianBlur(mSrcMat, mTargetMat, 9);
                    showResult("中斯模糊");
                }
            });
        }
    }

    private void avgBlur(){
        if(resetMat()){
            showProgress();
            ThreadUtils.runOnSubThread(new Runnable(){

                @Override
                public void run(){
                    Imgproc.blur(mSrcMat, mTargetMat, new Size(9,9));
                    showResult("均值模糊");
                }
            });
        }
    }
}
