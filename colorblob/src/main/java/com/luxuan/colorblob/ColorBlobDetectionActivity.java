package com.luxuan.colorblob;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ColorBlobDetectionActivity extends Activity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG ="OCVSample::Activity";

    private boolean mIsColorSelected=false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;

    private static final int PERMISSIONS_REQUEST_CAMERA =10011;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status){
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully.");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public ColorBlobDetectionActivity(){
        Log.i(TAG, "Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.color_blob_detection_surface_view);

        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        /*
         * 如果是6.0以上才去判断是否需要判断运行时权限,6.0以下不考虑
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
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
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }else{
            Log.d(TAG, "OpenCV library found inside package. Using it.");
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
    public void onCameraViewStarted(int width, int height){
        mRgba=new Mat(height, width, CvType.CV_8UC4);
        mDetector=new ColorBlobDetector();
        mSpectrum=new Mat();
        mBlobColorRgba=new Scalar(255);
        mBlobColorHsv=new Scalar(255);
        SPECTRUM_SIZE=new Size(200, 64);
        CONTOUR_COLOR=new Scalar(255,0,0,255);
    }

    @Override
    public void onCameraViewStopped(){
        mRgba.release();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){
        int cols=mRgba.cols();
        int rows=mRgba.rows();

        int xOffset=(mOpenCvCameraView.getWidth()-cols)/2;
        int yOffset=(mOpenCvCameraView.getHeight()-rows)/2;

        int x=(int)event.getX()-xOffset;
        int y=(int)event.getY()-yOffset;

        Log.i(TAG, "Touch image coordinates: ("+ x+", "+y+")");

        if((x<0)||(y<0)||(x>cols)||(y>rows)) {
            return false;
        }

        Rect touchedRect =new Rect();

        touchedRect.x=(x>4)?x-4:0;
        touchedRect.y=(y>4)?y-4:0;

        touchedRect.width=(x+4<cols)?x+4-touchedRect.x:cols-touchedRect.x;
        touchedRect.height=(y+4<rows)?y+4-touchedRect.y:rows-touchedRect.y;

        Mat touchedRegionRgba=mRgba.submat(touchedRect);

        Mat touchedRegionHsv=new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        mBlobColorHsv= Core.sumElems(touchedRegionHsv);
        int pointCount=touchedRect.width*touchedRect.height;
        for(int i=0;i<mBlobColorHsv.val.length;i++){
            mBlobColorHsv.val[i]/=pointCount;
        }

        mBlobColorRgba=convertScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touch rgba color: ("+mBlobColorRgba.val[0]+", "+ mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", "+ mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        mIsColorSelected=true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();

        if(mIsColorSelected){
            mDetector.process(mRgba);
            List<MatOfPoint> contours=mDetector.getContours();
            Log.e(TAG,"Contours count: "+contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            Mat colorLabel=mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel=mRgba.submat(4, 4+mSpectrum.rows(), 70, 70+mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor){
        Mat pointMatRgba=new Mat();
        Mat pointMatHsv=new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL);

        return new Scalar(pointMatRgba.get(0,0));
    }
}
