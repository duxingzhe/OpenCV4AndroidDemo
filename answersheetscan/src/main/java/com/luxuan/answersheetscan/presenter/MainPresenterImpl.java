package com.luxuan.answersheetscan.presenter;

import android.os.Environment;
import android.util.Log;

import com.luxuan.answersheetscan.view.MainActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;

public class MainPresenterImpl implements MainPresenter {

    private static final String TAG="MainPresenter";
    private LoaderCallbackInterface mLoaderCallback;
    private boolean NEED_CROP=false;
    private final int GALLERY_REQUEST_CODE=1;
    private final int CAMERA_REQUEST_CODE=2;
    private final int CROP_REQUEST_CODE=3;
    private final int ADVANCE_CROP_REQUEST_CODE=4;
    private final MainActivity mActivity;

    private File mTempFileDir=new File(Environment.getExternalStorageDirectory().getPath()+"/opencvdemo/cache");
    private File mTempFile;
    private File mCropFile;
    private String mPhotoFileName;
    private String mCropImgPath;

    public MainPresenterImpl(MainActivity activity){
        mActivity=activity;
    }

    @Override
    public void initOpenCV(){
        mLoaderCallback=new BaseLoaderCallback(mActivity.getApplicationContext()){
            @Override
            public void onManagerConnected(int status){
                switch(status){
                    case LoaderCallbackInterface.SUCCESS:
                        Log.e(TAG, "OpenCV loaded successfully");
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }

            @Override
            public void onPackageInstall(int operation, InstallCallbackInterface callback){

            }
        };

        if(!OpenCVLoader.initDebug()){
            Log.e(TAG,"Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, mActivity.getApplicationContext(),mLoaderCallback);
        }else{
            Log.e(TAG,"OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
