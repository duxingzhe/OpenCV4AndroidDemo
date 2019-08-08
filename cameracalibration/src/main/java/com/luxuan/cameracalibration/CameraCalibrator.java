package com.luxuan.cameracalibration;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

public class CameraCalibrator {

    private static final String TAG="OCVSample::CameraCalibrator";

    private final Size mPatternSize=new Size(4,11);
    private final int mConersSzie=(int)(mPatternSize.width*mPatternSize.height);
    private boolean mPatternWasFound=false;
    private MatOfPoint2f mCorners=new MatOfPoint2f();
    private List<Mat> mCornersBuffer=new ArrayList<Mat>();
    private boolean mIsCalibrated=false;

    private Mat mCameraMatrix=new Mat();
    private Mat mDistortionCoefficients=new Mat();
    private int mFlags;
    private double mRms;
    private double mSquareSize=0.0181;
    private Size mImageSize;

    public CameraCalibrator(int width, int height){
        mImageSize=new Size(width, height);

        mFlags= Calib3d.CALIB_FIX_PRINCIPAL_POINT+Calib3d.CALIB_ZERO_TANGENT_DIST+
                Calib3d.CALIB_FIX_ASPECT_RATIO+Calib3d.CALIB_FIX_K4+Calib3d.CALIB_FIX_K5;
        Mat.eye(3,3, CvType.CV_64FC1).copyTo(mCameraMatrix);
        mCameraMatrix.put(0,0,1.0);
        Mat.zeros(5,1, CvType.CV_64FC1).copyTo(mDistortionCoefficients);
        Log.i(TAG, "Instantiated new "+this.getClass());
    }

    public void processFrame(Mat grayFrame, Mat rgbaFrame){
        findPattern(grayFrame);
        renderFrame(rgbaFrame);
    }

    public void calibrate(){
        ArrayList<Mat> rvecs=new ArrayList<Mat>();
        ArrayList<Mat> tvecs=new ArrayList<Mat>();
        Mat reprojectionErrors=new Mat();
        ArrayList<Mat> objectPoints=new ArrayList<Mat>();
        objectPoints.add(Mat.zeros(mCornersSize, 1, CvType.CV_32FC3));
        calcBoardCornerPositions(objectPoints.get(0));
        for(int i=1;i<mCornersBuffer.size();i++){
            objectPoints.add(objectPoints.get(0));
        }

        Calib3d.calibrateCamera(objectPoints, mCornersBuffer, mImageSize, mCameraMatrix,
                mDistortionCoefficients, rvecs, tvecs, mFlags);

        mIsCalibrated= Core.checkRange(mCameraMatrix)&&Core.checkRange(mDistortionCoefficients);

        mRms=computeReprojectionErrors(objectPoints, rvecs, tvecs, reprojectionErrors);
        Log.i(TAG, String.format("Average re-projection error: %f", mRms));
        Log.i(TAG, "Camera matrix: "+mCameraMatrix.dump());
        Log.i(TAG, "Distortion coefficients: "+mDistortionCoefficients.dump());
    }
}
