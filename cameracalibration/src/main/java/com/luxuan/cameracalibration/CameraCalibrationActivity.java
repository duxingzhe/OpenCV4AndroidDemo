package com.luxuan.cameracalibration;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.luxuan.cameracalibration.Render.CalibrationFrameRender;
import com.luxuan.cameracalibration.Render.ComparisonFrameRender;
import com.luxuan.cameracalibration.Render.OnCameraFrameRender;
import com.luxuan.cameracalibration.Render.UndistortionFrameRender;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class CameraCalibrationActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private static final String TAG="OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private CameraCalibrator mCalibrator;
    private OnCameraFrameRender mOnCameraFrameRender;
    private int mWidth;
    private int mHeight;

    private static final int PERMISSIONS_REQUEST_CAMERA =10011;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status){
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraCalibrationActivity.this);
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public CameraCalibrationActivity(){
        Log.i(TAG, "Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.camera_calibration_surface_view);

        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.camera_calibration_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        /*
         * 如果是6.0以上才去判断是否需要判断运行时权限,6.0以下不考虑
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                return;
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }else{
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.calibration, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.preview_mode).setEnabled(true);
        if(!mCalibrator.isCalibrated()){
            menu.findItem(R.id.preview_mode).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.calibration:
                mOnCameraFrameRender=new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
                item.setChecked(true);
                return true;
            case R.id.undistortion:
                mOnCameraFrameRender=new OnCameraFrameRender(new UndistortionFrameRender(mCalibrator));
                item.setChecked(true);
                return true;
            case R.id.comparison:
                mOnCameraFrameRender=new OnCameraFrameRender(new ComparisonFrameRender(mCalibrator));
                item.setChecked(true);
                return true;
        }
    }
}
