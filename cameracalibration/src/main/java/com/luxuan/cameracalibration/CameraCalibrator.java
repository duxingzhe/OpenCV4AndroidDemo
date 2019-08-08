package com.luxuan.cameracalibration;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

public class CameraCalibrator {

    private static final String TAG="OCVSample::CameraCalibrator";

    private final Size mPatternSize=new Size(4,11);
    private final int mConersZie=(int)(mPatternSize.width*mPatternSize.height);
    private boolean mPatternWasFound=false;
    private MatOfPoint2f mCorners=new MatOfPoint2f();
    private List<Mat> mCornersbuffer=new ArrayList<Mat>();
    private boolean mIsCalibrated=false;

    private Mat mCameraMatrix=new Mat();
    private Mat mDistortionCoefficients=new Mat();
    private int mFlags;
    private double mRms;
    private double mSquareSize=0.0181;
    private Size mImageSize;


}
