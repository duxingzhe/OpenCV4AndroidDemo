package com.luxuan.opencv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FaceDetectorActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG="OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR=new Scalar(0, 255, 0, 255);
    private static final int JAVA_DETECTOR=0;
    private static final int NATIVE_DETECTOR=1;

    private MenuItem mItemFace50;
    private MenuItem mItemFace40;
    private MenuItem mItemFace30;
    private MenuItem mItemFace20;
    private MenuItem mItemType;

    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private int mDetectorType=JAVA_DETECTOR;
    private String[] mDetectorName;

    private float mRelativeFaceSize=0.2f;
    private int mAbsoluteFaceSize=0;

    private CameraBridgeViewBase mOpenCvCamerView;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this){

        @Override
        public void onManagerConnected(int status) {
            switch(status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    System.loadLibrary("detection_based_tracker");

                    try{
                        InputStream is=getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir=getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile=new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os=new FileOutputStream(mCascadeFile);

                        byte[] buffer=new byte[4096];
                        int bytesRead;
                        while((bytesRead=is.read(buffer))!=-1){
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector=new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if(mJavaDetector.empty()){
                            Log.e(TAG,"Failed to load cascade classifier");
                            mJavaDetector=null;
                        }
                        else
                        {
                            Log.i(TAG, "Loaded cascade classifier from "+ mCascadeFile.getAbsolutePath());
                        }

                        mNativeDetector=new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                    }catch(IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: "+ e);
                    }
                    mOpenCvCamerView.enableView();
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {
        mGray=new Mat();
        mRgba=new Mat();
    }

    @Override
    public void onCameraViewStopped(){
        mGray.release();
        mRgba.release();
    }
}
