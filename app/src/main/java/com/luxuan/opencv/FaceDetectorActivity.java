package com.luxuan.opencv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
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

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this){

        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case LoaderCallbackInterface.SUCCESS:
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
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public FaceDetectorActivity(){
        mDetectorName=new String[2];
        mDetectorName[JAVA_DETECTOR]="Java";
        mDetectorName[NATIVE_DETECTOR]="Native (tracking)";

        Log.i(TAG, "Instantiated new "+ this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_face_detect);

        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCvCameraView!=null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }else{
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mOpenCvCameraView.disableView();
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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        if(mAbsoluteFaceSize==0){
            int height=mGray.rows();
            if(Math.round(height*mRelativeFaceSize)>0){
                mAbsoluteFaceSize=Math.round(height*mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces=new MatOfRect();

        if(mDetectorType==JAVA_DETECTOR){
            if(mJavaDetector!=null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }else if(mDetectorType==NATIVE_DETECTOR){
            if(mNativeDetector!=null){
                mNativeDetector.detect(mGray, faces);
            }
        }

        Rect[] facesArray=faces.toArray();
        for(int i=0;i<facesArray.length;i++)
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50=menu.add("Face size 50%");
        mItemFace40=menu.add("Face size 40%");
        mItemFace30=menu.add("Face size 30%");
        mItemFace20=menu.add("Face size 20%");
        mItemType=menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i(TAG, "called onOptionsItemSelected; slectedItem item: "+item);
        if(item==mItemFace50){
            setMinFaceSize(0.5f);
        }else if(item==mItemFace40){
            setMinFaceSize(0.4f);
        }else if(item==mItemFace30){
            setMinFaceSize(0.3f);
        }else if(item==mItemFace20){
            setMinFaceSize(0.2f);
        }else if(item==mItemType){
            int tmpDetectorType=(mDetectorType+1)%mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
    }

    private void setMinFaceSize(float faceSize){
        mRelativeFaceSize=faceSize;
        mAbsoluteFaceSize=0;
    }

    private void setDetectorType(int type){
        if(mDetectorType!=type){
            mDetectorType=type;

            if(type==NATIVE_DETECTOR){
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            }else{
                Log.i(TAG, "Cascade detctor enbled");
                mNativeDetector.stop();
            }
        }
    }
}
